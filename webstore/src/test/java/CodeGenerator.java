import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/webstore?useSSL=false&serverTimezone=UTC",
                        "root",
                        "wxl123456")
                .globalConfig(builder -> {
                    builder.author("wxl")        // 作者
                            //.enableSwagger()         // 开启Swagger
                            //.fileOverride()          // 覆盖已生成文件
                            .outputDir("webstore\\src\\main\\java");  // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.wxl.webstore.order")  // 父包名
                            .moduleName("")     // 模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "src\\main\\resources\\mapper")); // XML位置
                })
                .strategyConfig(builder -> {
                    builder.addInclude("orders","order_item") // 要生成的表名
                            .addTablePrefix("") // 不忽略表前缀
                            .entityBuilder()//.enableFileOverride()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .controllerBuilder()
                            .enableRestStyle()
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .mapperBuilder()//.enableFileOverride()
                            .enableMapperAnnotation();
                })
                .templateEngine(new FreemarkerTemplateEngine() {
                    @Override
                    public String templateFilePath(String filePath) {
                        // 使用自定义模板
                        if (filePath.endsWith("entity.java.ftl")) {
                            return "templates/entity.java.ftl"; // 指向自定义模板
                        }
                        return super.templateFilePath(filePath);
                    }
                })
                .execute();
    }
}