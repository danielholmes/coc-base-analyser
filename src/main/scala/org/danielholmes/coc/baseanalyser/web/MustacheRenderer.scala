package org.danielholmes.coc.baseanalyser.web

import java.io.StringWriter

import com.github.mustachejava.MustacheFactory

class MustacheRenderer(val mustacheFactory: MustacheFactory) {
  def render(name: String, vars: Map[String, Any]): String = {
    val mustache = mustacheFactory.compile(name)

    val writer = new StringWriter()
    mustache.execute(writer, vars).flush()

    writer.toString
  }
}
