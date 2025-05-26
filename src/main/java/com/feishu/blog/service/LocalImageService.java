package com.feishu.blog.service;

import com.feishu.blog.dto.ClassificationDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Batchifier;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.translate.Pipeline;
import org.springframework.web.multipart.MultipartFile;


//@Service
//public class LocalImageService {
////    @Autowiredd
////    private ImagePreprocessor preprocessor;
//
//    private ZooModel<Image, Classifications> model;
//
//
//    public Image preprocessImage(byte[] imageData) throws IOException {
//        // 仅加载图像，不进行预处理
//        return ImageFactory.getInstance().fromInputStream(new ByteArrayInputStream(imageData));
//    }
//
//    @PostConstruct
//    public void init() throws ModelException, IOException {
//        // 自定义 Translator
//        class CustomImageClassificationTranslator implements Translator<Image, Classifications> {
//            private Pipeline pipeline;
//            private List<String> classes;
//
//            public CustomImageClassificationTranslator() {
//                pipeline = new Pipeline()
//                        .add(new Resize(256, 256)) // 调整大小为 256x256
//                        .add(new ToTensor());      // 转换为张量
//                classes = Arrays.asList("cat", "dog", "other");
//            }
//
//            @Override
//            public NDList processInput(TranslatorContext ctx, Image input) {
//                // 将 Image 转换为 NDArray 并应用 Pipeline
//                NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
//                NDList inputList = new NDList(array);
//                NDList transformed = pipeline.transform(inputList);
//                return transformed;
//            }
//
//            @Override
//            public Classifications processOutput(TranslatorContext ctx, NDList output) {
//                NDArray probabilities = output.get(0).softmax(0);
//                return new Classifications(classes, probabilities);
//            }
//
//            @Override
//            public Batchifier getBatchifier() {
//                return Batchifier.STACK;
//            }
//        }
//
//        // 加载模型
//        Criteria<Image, Classifications> criteria = Criteria.builder()
//                .optApplication(Application.CV.IMAGE_CLASSIFICATION)
//                .setTypes(Image.class, Classifications.class)
//                .optModelPath(new ClassPathResource("static/model.onnx").getFile().toPath())
//                .optModelName("model.onnx")
//                .optEngine("OnnxRuntime")
//                .optTranslator(new CustomImageClassificationTranslator())
//                .optProgress(new ProgressBar())
//                .build();
//        model = ModelZoo.loadModel(criteria);
//    }
//
//    public ImageClassificationDTO classifyImage(MultipartFile file) throws Exception {
//        // 获取原始图像
//        byte[] imageData = file.getBytes();
//        Image img = preprocessImage(imageData);
//
//        // 进行推理
//        try (Predictor<Image, Classifications> predictor = model.newPredictor()) {
//            long startTime = System.nanoTime(); // 记录开始时间
//            Classifications result = predictor.predict(img);
//            long endTime = System.nanoTime(); // 记录结束时间
//            double inferenceTimeMs = (endTime - startTime) / 1_000_000.0; // 转换为毫秒
//            System.out.println("Inference time: " + inferenceTimeMs + " ms");
////            initialization in 2 ms
////            Inference time: 98.0237 ms
////            Inference time: 34.093 ms
//            // 构建类别信息的字符串并打印每个类别的概率
//            StringBuilder msgBuilder = new StringBuilder();
//            for (Classifications.Classification classification : result.items()) {
//                msgBuilder.append(String.format("Class: %s, Probability: %.4f; ",
//                        classification.getClassName(), classification.getProbability()));
//            }
//
//            // 获取最有可能的类别
//            String bestCategory = result.best().getClassName();
//
//            // 判断is_valid字段的值，如果类别是"Cat"或"Dog"则返回false，否则返回true
//            boolean isValid = !bestCategory.equals("Cat") && !bestCategory.equals("Dog");
//
//            // 返回响应体
//            ImageClassificationDTO.Data data = new ImageClassificationDTO.Data(isValid, bestCategory);
//            return new ImageClassificationDTO(0, msgBuilder.toString(), data);
//        }
//    }
//}


@Service
public class LocalImageService {
//    @Autowired
//    private ImagePreprocessor preprocessor;

    private ZooModel<Image, Classifications> model;

    public Image preprocessImage(byte[] imageData) throws IOException {
        // 仅加载图像，不进行预处理
        return ImageFactory.getInstance().fromInputStream(new ByteArrayInputStream(imageData));
    }

    public static MultipartFile fileToMultipartFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream input = new FileInputStream(file);
        return new MockMultipartFile(
                file.getName(), // 文件名
                file.getName(), // 原始文件名
                "image/jpeg",   // MIME 类型，可以根据需要修改
                input           // 文件流
        );
    }

    @PostConstruct
    public void init() throws ModelException, IOException {
        // 自定义 Translator
        class CustomImageClassificationTranslator implements Translator<Image, Classifications> {
            private Pipeline pipeline;
            private List<String> classes;

            public CustomImageClassificationTranslator() {
                pipeline = new Pipeline()
                        .add(new Resize(256, 256)) // 调整大小为 256x256
                        .add(new ToTensor());      // 转换为张量
                classes = Arrays.asList("cat", "dog", "other");
            }

            @Override
            public NDList processInput(TranslatorContext ctx, Image input) {
                // 将 Image 转换为 NDArray 并应用 Pipeline
                NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
                NDList inputList = new NDList(array);
                NDList transformed = pipeline.transform(inputList);
                return transformed;
            }

            @Override
            public Classifications processOutput(TranslatorContext ctx, NDList output) {
                NDArray probabilities = output.get(0).softmax(0);
                return new Classifications(classes, probabilities);
            }

            @Override
            public Batchifier getBatchifier() {
                return Batchifier.STACK;
            }
        }

        // 加载模型
        Criteria<Image, Classifications> criteria = Criteria.builder()
                .optApplication(Application.CV.IMAGE_CLASSIFICATION)
                .setTypes(Image.class, Classifications.class)
                .optModelPath(new ClassPathResource("static/model.onnx").getFile().toPath())
                .optModelName("model.onnx")
                .optEngine("OnnxRuntime")
                .optTranslator(new CustomImageClassificationTranslator())
                .optProgress(new ProgressBar())
                .build();
        model = ModelZoo.loadModel(criteria);
    }

    public ClassificationDTO classifyImage(MultipartFile file) throws Exception {
        // 获取原始图像
        byte[] imageData = file.getBytes();
        Image img = preprocessImage(imageData);

        // 进行推理
        try (Predictor<Image, Classifications> predictor = model.newPredictor()) {
            long startTime = System.nanoTime(); // 记录开始时间
            Classifications result = predictor.predict(img);
            long endTime = System.nanoTime(); // 记录结束时间
            double inferenceTimeMs = (endTime - startTime) / 1_000_000.0; // 转换为毫秒
            System.out.println("Inference time: " + inferenceTimeMs + " ms");
//            initialization in 2 ms
//            Inference time: 98.0237 ms
//            Inference time: 34.093 ms
            // 构建类别信息的字符串并打印每个类别的概率
            StringBuilder msgBuilder = new StringBuilder();
            for (Classifications.Classification classification : result.items()) {
                msgBuilder.append(String.format("Class: %s, Probability: %.4f; ",
                        classification.getClassName(), classification.getProbability()));
            }

            // 获取最有可能的类别
            String bestCategory = result.best().getClassName();

            // 判断is_valid字段的值，如果类别是"Cat"或"Dog"则返回false，否则返回true
            boolean isValid = !bestCategory.equals("cat") && !bestCategory.equals("dog");

            // 返回响应体
            ClassificationDTO.Data data = new ClassificationDTO.Data(isValid, bestCategory);
            return new ClassificationDTO(0, msgBuilder.toString(), data);
        }
    }

    public ClassificationDTO classifyImageWithPath(String path) throws Exception {
        MultipartFile file = fileToMultipartFile(path);
        return classifyImage(file);
    }
}