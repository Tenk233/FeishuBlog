package com.feishu.blog.service;

import ai.djl.ModelException;
import ai.djl.huggingface.tokenizers.*;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.*;
import ai.djl.ndarray.types.DataType;
import ai.djl.repository.zoo.*;
import ai.djl.ndarray.types.Shape;
import com.feishu.blog.dto.SentimentDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@Service
public class LocalSentimentService {
    private ZooModel<NDList, NDList> model;
    private Predictor<NDList, NDList> predictor;
    private HuggingFaceTokenizer tokenizer;
    private final Map<Integer, String> id2label = Map.of(
            0, "积极",
            1, "消极",
            2, "中性"
    );
    List<String> validCategories = List.of("积极", "中性");
    private boolean isValidCategory(String category) {
        return validCategories.contains(category);
    }

    @PostConstruct
    public void init() throws ModelException, IOException {
        ClassPathResource tokenizerRes = new ClassPathResource("static/BertTokenizer/tokenizer.json");
        Path tempFile = Files.createTempFile("tokenizer", ".json");
        Files.copy(tokenizerRes.getInputStream(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        tokenizer = HuggingFaceTokenizer.newInstance(tempFile);

        ClassPathResource modelRes = new ClassPathResource("static/minirbt.onnx");
        Path modelPath = Files.createTempFile("model", ".onnx");
        Files.copy(modelRes.getInputStream(), modelPath, StandardCopyOption.REPLACE_EXISTING);

        Criteria<NDList, NDList> criteria = Criteria.builder()
                .setTypes(NDList.class, NDList.class)
                .optModelPath(modelPath)
                .optEngine("OnnxRuntime") // 指定 ONNX 引擎
                .build();

        model = ModelZoo.loadModel(criteria);
        predictor = model.newPredictor();
    }

    private String classify(String text) throws Exception {
        try (NDManager manager = NDManager.newBaseManager()) {
            Encoding encoding = tokenizer.encode(text);
            long[] inputIdsArr = encoding.getIds();
            long[] attentionMaskArr = encoding.getAttentionMask();

            NDArray inputIds = manager.create(inputIdsArr)
                    .toType(DataType.INT64, false)
                    .reshape(new Shape(1, inputIdsArr.length)); // batch_size=1

            NDArray attentionMask = manager.create(attentionMaskArr)
                    .toType(DataType.INT64, false)
                    .reshape(new Shape(1, attentionMaskArr.length));

            NDList input = new NDList(inputIds, attentionMask);
            NDList output = predictor.predict(input);
            NDArray logits = output.get(0);

            NDArray predArray = logits.argMax(1);
            int pred = predArray.toType(DataType.INT32, false).getInt(0);
            return id2label.get(pred);
        }
    }

    public SentimentDTO processContentCompliance(String inputText) {
        SentimentDTO response = new SentimentDTO();

        try {
            String category = classify(inputText);
            response.setData(new SentimentDTO.Data(isValidCategory(category),category));
            response.setCode(0);
            response.setMsg("Success");
        } catch (Exception e) {
            response.setCode(1);
            response.setData(new SentimentDTO.Data(false,"未知"));
            response.setMsg(e.getMessage());
        }

        return response;
    }

    @PreDestroy
    public void close() {
        if (predictor != null) {
            predictor.close();
        }
        if (model != null) {
            model.close();
        }
    }
}
