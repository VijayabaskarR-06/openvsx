package org.eclipse.openvsx.search;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.openvsx.entities.Extension;
import org.eclipse.openvsx.repositories.RepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ElasticSearchNamespaceFilterIT {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    void namespaceFilterMatchesOnlyExactNamespace() {
        Extension vscode = new Extension();
        vscode.setNamespace("vscode");
        vscode.setName("python");
        repositoryService.save(vscode);

        Extension msVscode = new Extension();
        msVscode.setNamespace("ms-vscode");
        msVscode.setName("cpptools");
        repositoryService.save(msVscode);

        elasticSearchService.updateSearchIndex(true);

        Options options = new Options(
                null,
                "vscode",
                null,
                null,
                null,
                0,
                10,
                "desc",
                SortBy.RELEVANCE
        );

        SearchResult result = elasticSearchService.search(options);
        List<ExtensionSearch> extensions = result.extensions();

        assertThat(extensions)
                .extracting(ExtensionSearch::getNamespace)
                .contains("vscode")
                .doesNotContain("ms-vscode");
    }
}
