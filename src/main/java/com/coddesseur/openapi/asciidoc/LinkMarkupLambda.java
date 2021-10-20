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
 * Lambda emitting an asciidoc "http link" if file is found in path. Use:
 *
 * <pre>
 * {{#snippetLink}}markup until koma, /{{name}}.json{{/snippetLink}}
 * </pre>
 */
public class LinkMarkupLambda implements Mustache.Lambda {

  private final Logger LOGGER = LoggerFactory.getLogger(LinkMarkupLambda.class);

  private final String basePath;
  private long linkedCount = 0;
  private long notFoundLinkCount = 0;

  public LinkMarkupLambda(final String basePath) {
    this.basePath = basePath;
  }

  @Override
  public void execute(final Template.Fragment frag, final Writer out) throws IOException {

    String content = frag.execute();
    String[] tokens = content.split(",", 2);

    String linkName = tokens.length > 0 ? tokens[0] : "";

    String relativeFileName = AsciidocCodegen
        .sanitize(tokens.length > 1 ? tokens[1] : linkName);

    final Path filePathToLinkTo = Paths.get(basePath, relativeFileName).toAbsolutePath();

    if (Files.isRegularFile(filePathToLinkTo)) {
      LOGGER.debug("linking {}. file into markup from: {}", ++linkedCount, filePathToLinkTo);
      out.write("\n" + linkName + " link:" + relativeFileName + "[]\n");
    } else {
      LOGGER.debug("{}. file not found, skip link for: {}", ++notFoundLinkCount, filePathToLinkTo);
      out.write("\n// file not found, no " + linkName + " link :" + relativeFileName + "[]\n");
    }
  }
}
