package com.getjenny.starchat.analyzer.atoms

import com.getjenny.starchat.analyzer.utils.VectorUtils._
import com.getjenny.analyzer.atoms.AbstractAtomic
import com.getjenny.starchat.entities._

import scala.concurrent.{Await, ExecutionContext, Future}
import com.getjenny.starchat.services._

import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global

/**
  * Created by mal on 20/02/2017.
  */

class W2VCosineSentenceAtomic(val sentence: String) extends AbstractAtomic  {
  /**
    * cosine distance between sentences renormalized at [0, 1]: (cosine + 1)/2
    *
    * state_lost_password_cosine = Cosine("lost password")
    * state_lost_password_cosine.evaluate("I'm desperate, I've lost my password")
    *
    */

  val termService = new TermService

  val empty_vec = Vector.fill(300){0.0}
  def getTextVector(text: String): (Vector[Double], Double) = {
    val text_vectors = termService.textToVectors(text)
    val vector = text_vectors match {
      case Some(t) => {
        val vectors = t.terms.get.terms.map(e => e.vector.get).toVector
        val sentence_vector =
          if (vectors.length > 0) sumArrayOfArrays(vectors) else empty_vec
        val reliability_factor =
          text_vectors.get.terms_found_n.toDouble / text_vectors.get.text_terms_n.toDouble
        (sentence_vector, reliability_factor)
      }
      case _ => (empty_vec, 0.0) //default dimension
    }
    vector
  }

  val sentence_vector = getTextVector(sentence)

  override def toString: String = "similar(\"" + sentence + "\")"
  val isEvaluateNormalized: Boolean = true
  def evaluate(query: String): Double = {
    val query_vector = getTextVector(query)
    val distance = (1.0 - cosineDist(sentence_vector._1, query_vector._1)) *
      (sentence_vector._2 * query_vector._2)
    distance
  }

  // Similarity is normally the cosine itself. The threshold should be at least
  // angle < pi/2 (cosine > 0), but for synonyms let's put cosine > 0.6, i.e. self.evaluate > 0.8
  override val match_threshold: Double = 0.8
}
