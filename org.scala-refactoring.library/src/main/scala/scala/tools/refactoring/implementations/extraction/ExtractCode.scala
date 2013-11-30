package scala.tools.refactoring.implementations.extraction

abstract class ExtractCode extends ExtractionRefactoring with AutoExtractions {
  import global._

  type E = Extraction

  val collector = AutoExtraction

  case class RefactoringParameters(
    selectedExtraction: E,
    name: String)

  def perform(s: Selection, prepared: PreparationResult, params: RefactoringParameters) =
    perform(params.selectedExtraction, params.name)
}

trait AutoExtractions extends MethodExtractions with ValueExtractions {
  object AutoExtraction extends ExtractionCollector[Extraction] {
    def prepareExtractionSource(s: Selection) =
      MethodExtraction.prepareExtractionSource(s)

    def prepareExtractions(source: Selection, targets: List[ExtractionTarget]) = {
      val valueTargets =
        if (source.mayHaveSideEffects) Nil
        else ValueExtraction.validTargets(source, targets)
      val methodTargets =
        MethodExtraction.validTargets(source, targets diff valueTargets)

      if (valueTargets.isEmpty && methodTargets.isEmpty)
        Left(noExtractionMsg)
      else
        Right(
          valueTargets.map(ValueExtraction(source, _)) :::
            methodTargets.map(MethodExtraction(source, _)))
    }

  }
}