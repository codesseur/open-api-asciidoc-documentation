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

public class StatusCodeLambda implements Mustache.Lambda {

  @Override
  public void execute(final Template.Fragment frag, final Writer out) throws IOException {

    String content = frag.execute().replaceAll("/.*", "");
    out.write(content);
  }
}
