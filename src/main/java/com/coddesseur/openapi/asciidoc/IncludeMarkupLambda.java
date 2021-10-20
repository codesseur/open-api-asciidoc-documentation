package com.coddesseur.openapi.asciidoc;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lambda emitting an asciidoc "include::filename.adoc[]" if file is found in path. Use:
 *
 * <pre>
 * {{#includemarkup}}{{name}}/description.adoc{{/includemarkup}}
 * </pre>
 */
public class IncludeMarkupLambda implements Mustache.Lambda {

  private final Logger LOGGER = LoggerFactory.getLogger(IncludeMarkupLambda.class);

  private final String basePath;

  private long includeCount = 0;
  private long notFoundCount = 0;

  public IncludeMarkupLambda(String basePath) {
    LOGGER.info("@@@@@@@basePath {}", basePath);
    this.basePath = basePath;
  }

  public String resetCounter() {
    String msg = "included: " + includeCount + " notFound: " + notFoundCount + " from " + basePath;
    includeCount = 0;
    notFoundCount = 0;
    return msg;
  }

  @Override
  public void execute(final Template.Fragment frag, final Writer out) throws IOException {

    final String relativeFileName = AsciidocCodegen.sanitize(frag.execute());
    final Path filePathToInclude = Paths.get(basePath, relativeFileName).toAbsolutePath();

    String includeStatement =
        "include::" + escapeCurlyBrackets(relativeFileName) + "[opts=optional]";
    if (Files.isRegularFile(filePathToInclude)) {
      LOGGER.info("including {}. file into markup from: {}", ++includeCount, filePathToInclude);
      out.write("\n" + includeStatement + "\n");
    } else {
      LOGGER.warn("{}. file not found, skip include for: {}", ++notFoundCount, filePathToInclude);
      out.write("\n// markup not found, no " + includeStatement + "\n");
    }
  }

  private String escapeCurlyBrackets(String relativeFileName) {
    return relativeFileName.replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}");
  }
}
