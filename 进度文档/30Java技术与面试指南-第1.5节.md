# Java与AI集成 (第1.5节)

## 1.5 Java与AI集成

随着人工智能的快速发展，Java生态系统中也出现了多种AI集成解决方案：

### 1.5.1 深度学习框架

Java生态系统中可用的多种深度学习框架：

**Deep Java Library (DJL)**

Amazon开发的DJL是一个高级深度学习API，支持多种后端（PyTorch、TensorFlow、MXNet等）：

```java
// 使用DJL进行图像分类
// 加载预训练模型
Criteria<Image, Classifications> criteria = Criteria.builder()
    .setTypes(Image.class, Classifications.class)
    .optArtifactId("ai.djl.pytorch:pytorch-model-zoo")
    .optFilter("backbone", "resnet50")
    .optProgress(new ProgressBar())
    .build();

ZooModel<Image, Classifications> model = ModelZoo.loadModel(criteria);
Predictor<Image, Classifications> predictor = model.newPredictor();

// 加载图像并进行预测
Image img = ImageFactory.getInstance().fromUrl("https://example.com/image.jpg");
Classifications predictions = predictor.predict(img);

// 输出预测结果
predictions.items().forEach(item -> 
    System.out.printf("%s: %.5f\n", item.getClassName(), item.getProbability()));
```

**TensorFlow Java**

Google的TensorFlow也提供了Java API：

```java
// 使用TensorFlow Java API
import org.tensorflow.*;

public class TensorFlowExample {
    
    public static void main(String[] args) throws Exception {
        // 加载保存的模型
        try (SavedModelBundle model = SavedModelBundle.load("path/to/model", "serve")) {
            // 创建Tensor
            try (Tensor<Float> input = Tensor.create(
                    new long[] {1, 224, 224, 3}, 
                    FloatBuffer.wrap(prepareImageData()))) {
                    
                // 运行推理
                List<Tensor<?>> result = model.session().runner()
                    .feed("input_tensor", input)
                    .fetch("output_tensor")
                    .run();
                    
                // 处理结果
                try (Tensor<Float> output = (Tensor<Float>) result.get(0)) {
                    float[] probabilities = output.copyTo(new float[1][1000])[0];
                    printTopPredictions(probabilities, labels, 5);
                }
            }
        }
    }
}
```

### 1.5.2 大语言模型集成

Java应用中整合大语言模型的工具与框架：

**LangChain4j**

LangChain的Java实现，用于构建LLM应用：

```java
// 使用LangChain4j集成OpenAI
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

// 定义AI服务接口
public interface Assistant {
    String chat(String userMessage);
}

// 实例化
OpenAiChatModel model = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .modelName("gpt-4")
    .build();
    
// 创建AI服务
Assistant assistant = AiServices.builder(Assistant.class)
    .chatLanguageModel(model)
    .build();
    
// 使用服务
String response = assistant.chat("Java虚拟线程的优势是什么？");
System.out.println(response);
```

**知识检索增强RAG（Retrieval Augmented Generation）**

```java
// 使用向量存储和LLM实现RAG
// 基于PgVector的知识库
public class KnowledgeBaseService {
    
    private final JdbcTemplate jdbcTemplate;
    private final OpenAiEmbeddingModel embeddingModel;
    private final ChatLanguageModel chatModel;
    
    // 存储文档
    public void storeDocument(String documentText) {
        // 文档切片
        List<TextSegment> segments = TextSegmenter.segment(documentText);
        
        // 生成向量嵌入
        for (TextSegment segment : segments) {
            Embedding embedding = embeddingModel.embed(segment.text()).content();
            
            // 存储到数据库
            jdbcTemplate.update(
                "INSERT INTO knowledge_base (content, embedding) VALUES (?, ?)",
                segment.text(), embedding.vectorAsPgVector());
        }
    }
    
    // 检索知识库
    public String query(String userQuestion) {
        // 将问题转换为向量
        Embedding questionEmbedding = embeddingModel.embed(userQuestion).content();
        
        // 检索相关文档
        List<String> relevantDocs = jdbcTemplate.query(
            "SELECT content FROM knowledge_base ORDER BY embedding <=> ? LIMIT 3",
            (rs, rowNum) -> rs.getString("content"),
            questionEmbedding.vectorAsPgVector());
            
        // 组合上下文
        String context = String.join("\n\n", relevantDocs);
        
        // 调用LLM生成回答
        String prompt = "Based on the following information, answer the user's question.\n\n" +
                       "Information: " + context + "\n\n" +
                       "User question: " + userQuestion;
                       
        return chatModel.generate(prompt);
    }
}
```

**调用国内大模型（文心一言、通义千问）**

```java
// 文心一言API调用
@Service
public class ErnieService {
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String secretKey;
    private String accessToken;
    
    @Scheduled(fixedRate = 86400000) // 每天24小时刷新token
    public void refreshToken() {
        String url = "https://aip.baidubce.com/oauth/2.0/token?" +
                    "grant_type=client_credentials&" +
                    "client_id=" + apiKey + "&" +
                    "client_secret=" + secretKey;
                    
        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
        this.accessToken = (String) response.getBody().get("access_token");
    }
    
    public String generateContent(String prompt) {
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + accessToken;
        
        Map<String, Object> requestBody = Map.of(
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "temperature", 0.7
        );
        
        ResponseEntity<ErnieResponse> response = 
            restTemplate.postForEntity(url, requestBody, ErnieResponse.class);
            
        return response.getBody().getResult();
    }
}
```

### 1.5.3 实时计算与优化

**模型服务器集成**

```java
// 使用ONNX Runtime运行模型
@Service
public class ImageClassificationService {

    private OrtEnvironment env;
    private OrtSession session;

    @PostConstruct
    public void init() throws Exception {
        env = OrtEnvironment.getEnvironment();
        session = env.createSession("models/image_classifier.onnx", new OrtSession.SessionOptions());
    }

    public List<Classification> classifyImage(byte[] imageBytes) {
        try {
            // 预处理图像
            float[] processedImage = preprocessImage(imageBytes);
            
            // 创建Tensor
            OnnxTensor tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(processedImage), 
                                                     new long[]{1, 3, 224, 224});
            
            // 运行推理
            Map<String, OnnxTensor> inputs = Map.of("input", tensor);
            OrtSession.Result result = session.run(inputs);
            
            // 处理结果
            float[] probabilities = ((float[][]) result.get(0).getValue())[0];
            return interpretResults(probabilities);
            
        } catch (Exception e) {
            throw new RuntimeException("Image classification failed", e);
        }
    }
}
```

**并行数据处理**

```java
// 使用Java并行流处理大数据
@Service
public class DataProcessingService {

    public List<AnalysisResult> processLargeDataset(List<DataPoint> data) {
        return data.parallelStream()
            .filter(this::isValid)
            .map(this::preprocess)
            .map(this::analyze)
            .collect(Collectors.toList());
    }
    
    private boolean isValid(DataPoint point) {
        // 验证逻辑
        return point != null && point.getValue() != null;
    }
    
    private DataPoint preprocess(DataPoint point) {
        // 数据预处理逻辑
        return new DataPoint(
            point.getId(),
            normalize(point.getValue()),
            point.getTimestamp()
        );
    }
    
    private AnalysisResult analyze(DataPoint point) {
        // 应用ML模型或分析算法
        return new AnalysisResult(
            point.getId(),
            runModel(point.getValue()),
            System.currentTimeMillis()
        );
    }
}
```

### 1.5.4 大模型应用集成架构

在Java应用中集成大语言模型的架构模式：

**智能简历助手应用架构示例**

```java
@Service
public class AIEnhancedResumeService {

    private final ResumeRepository resumeRepository;
    private final OpenAIClient openAIClient; // 或其他LLM客户端
    private final UserPreferenceService preferenceService;
    
    public ResumeEnhancementResult enhanceResume(Long resumeId, Long userId) {
        // 获取简历和用户偏好
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
            
        UserPreference preferences = preferenceService.getPreferencesByUserId(userId);
        
        // 准备提示词
        String prompt = buildResumeEnhancementPrompt(resume, preferences);
        
        // 调用LLM服务
        LLMResponse response = openAIClient.generateCompletion(prompt);
        
        // 解析和应用建议
        List<EnhancementSuggestion> suggestions = parseSuggestions(response.getText());
        
        // 返回结果
        return new ResumeEnhancementResult(resume, suggestions);
    }
    
    private String buildResumeEnhancementPrompt(Resume resume, UserPreference preferences) {
        return String.format(
            "As a professional resume writer, please enhance the following resume sections " +
            "for a %s position in %s industry. The candidate has %d years of experience. " +
            "Focus on these aspects: %s.\n\n" +
            "Current resume:\n%s\n\n" +
            "Provide specific improvement suggestions for each section.",
            preferences.getTargetPosition(),
            preferences.getTargetIndustry(),
            preferences.getYearsOfExperience(),
            String.join(", ", preferences.getFocusAreas()),
            resume.getContent()
        );
    }
    
    private List<EnhancementSuggestion> parseSuggestions(String llmResponse) {
        // 解析LLM响应并将其转换为结构化建议
        // 实际实现可能使用正则表达式、NLP处理或JSON解析
        // ...
        return suggestions;
    }
}
```

**智能代理模式**

```java
// 一个基于Java的AI代理系统
@Service
public class ResumeAssistantAgent {

    private final LLMService llmService;
    private final ToolRegistry toolRegistry;
    private final MemoryService memoryService;
    
    public AgentResponse processUserQuery(String userQuery, String userId) {
        // 1. 检索用户上下文和历史
        ConversationContext context = memoryService.loadUserContext(userId);
        
        // 2. 准备提示词
        String systemPrompt = "You are a professional resume assistant helping users improve their job applications.";
        String prompt = buildAgentPrompt(systemPrompt, context, userQuery);
        
        // 3. 调用LLM进行意图识别和规划
        AgentPlan plan = llmService.planExecution(prompt);
        
        // 4. 执行工具调用
        for (ToolCall toolCall : plan.getToolCalls()) {
            Tool tool = toolRegistry.getTool(toolCall.getToolName());
            Object result = tool.execute(toolCall.getParameters());
            plan.addResult(toolCall.getId(), result);
        }
        
        // 5. 生成最终响应
        String finalResponse = llmService.generateResponse(plan);
        
        // 6. 更新用户上下文
        memoryService.updateUserContext(userId, userQuery, finalResponse);
        
        return new AgentResponse(finalResponse, plan.getExecutedTools());
    }
}
```

**多模态集成**

```java
// 多模态处理服务
@Service
public class MultimodalResumeService {

    private final ImageAnalysisService imageService;
    private final TextProcessingService textService;
    private final DocumentLayoutService layoutService;
    private final LLMService llmService;
    
    // 分析简历文件（PDF/图像）
    public ResumeAnalysis analyzeResumeDocument(MultipartFile file) {
        // 1. 提取文本和图像
        DocumentContent content = layoutService.extractContent(file);
        
        // 2. OCR处理（如果是图像）
        String extractedText = textService.processText(content.getText());
        
        // 3. 分析图像元素（如照片、图表）
        List<VisualElement> visualElements = imageService.analyzeVisualElements(content.getImages());
        
        // 4. 结构化简历数据
        ResumeStructure structure = textService.parseResumeStructure(extractedText);
        
        // 5. 使用LLM进行综合分析
        String prompt = buildMultimodalAnalysisPrompt(structure, visualElements);
        AnalysisResponse analysis = llmService.analyze(prompt);
        
        // 6. 返回结构化结果
        return new ResumeAnalysis(
            structure,
            visualElements,
            analysis.getSkills(),
            analysis.getExperience(),
            analysis.getRecommendations()
        );
    }
}
```
