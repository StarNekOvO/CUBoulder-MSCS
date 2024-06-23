package io.collective.articles;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.restsupport.BasicHandler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ArticlesController extends BasicHandler {
    private final ArticleDataGateway gateway;

    public ArticlesController(ObjectMapper mapper, ArticleDataGateway gateway) {
        super(mapper);
        this.gateway = gateway;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        get("/articles", List.of("application/json", "text/html"), baseRequest, response, () -> {
            List<ArticleRecord> articles = gateway.findAll();
            writeJsonBody(response, articles);
        });

        get("/available", List.of("application/json"), baseRequest, response, () -> {
            List<ArticleRecord> availableArticles = gateway.findAvailable();
            writeJsonBody(response, availableArticles);
        });
    }
}