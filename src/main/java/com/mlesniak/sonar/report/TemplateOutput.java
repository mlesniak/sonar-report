package com.mlesniak.sonar.report;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.List;

/**
 * Processes a list of issues and generates HTML output.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class TemplateOutput {
    private TemplateEngine templateEngine;
    private final Configuration config;
    private String templateFileName;


    public TemplateOutput() {
        config = Configuration.get();

        ITemplateResolver templateResolver = determineTemplateResolver();
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    private ITemplateResolver determineTemplateResolver() {
        templateFileName = config.get("template");
        if (templateFileName == null) {
            templateFileName = "classpath:defaultTemplate.html";
        }
        ITemplateResolver templateResolver;
        if (templateFileName.startsWith("classpath:")) {
            templateFileName = templateFileName.substring("classpath:".length());
            templateResolver = new ClassLoaderTemplateResolver();
        } else {
            templateResolver = new FileTemplateResolver();
        }
        return templateResolver;
    }

    public String processIssues(List<SonarConnection.Issue> issues) {
        Context context = new Context();
        context.setVariable("issues", issues);
        return templateEngine.process(templateFileName, context);
    }
}
