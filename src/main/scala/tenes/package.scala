package object tenes {

  case class Racquet(
      name: String,
      headSize: Int,
      weight: Float,
      stiffness: Int,
      length: Double,
      beam: Int,
      balance: String,
      balanceP1: Int,
      balanceP2: String,
      url: String = "",
      desc: String
  )

  case class Brand(
      name: String,
      href: String
  )

  def slugify(input: String): String = {
    import java.text.Normalizer
    Normalizer
      .normalize(input, Normalizer.Form.NFD)
      .replaceAll("[^\\w\\s-]", "") // Remove all non-word, non-space or non-dash characters
      .replace('-', ' ') // Replace dashes with spaces
      .trim // Trim leading/trailing whitespace (including what used to be leading/trailing dashes)
      .replaceAll("\\s+", "-") // Replace whitespace (including newlines and repetitions) with single dashes
      .toLowerCase // Lowercase the final results
  }
}
