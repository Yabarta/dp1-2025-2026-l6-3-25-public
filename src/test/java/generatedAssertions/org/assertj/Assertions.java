package generatedAssertions.org.assertj;

import generatedAssertions.es.us.dp1.l6_3_24_25.Petris.match.model.PetrismatchmodelTurnTypeAssert;
import generatedAssertions.es.us.dp1.l6_3_24_25.Petris.match.model.PetrismatchmodelMatchAssert;
import generatedAssertions.es.us.dp1.l6_3_24_25.Petris.match.model.PetrismatchmodelPetriDishAssert;
import jakarta.annotation.Generated;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
@Generated(value="assertj-assertions-generator")
public class Assertions {

  /**
   * Creates a new instance of <code>{@link es.us.dp1.l6_3_24_25.Petris.match.model.PetrismatchmodelMatchAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @SuppressWarnings("deprecation")
  @org.assertj.core.util.CheckReturnValue
  public static PetrismatchmodelMatchAssert assertThat(es.us.dp1.l6_3_24_25.Petris.match.model.Match actual) {
    return new PetrismatchmodelMatchAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link es.us.dp1.l6_3_24_25.Petris.match.model.PetrismatchmodelPetriDishAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @SuppressWarnings("deprecation")
  @org.assertj.core.util.CheckReturnValue
  public static PetrismatchmodelPetriDishAssert assertThat(es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish actual) {
    return new PetrismatchmodelPetriDishAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link es.us.dp1.l6_3_24_25.Petris.match.model.PetrismatchmodelTurnTypeAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @SuppressWarnings("deprecation")
  @org.assertj.core.util.CheckReturnValue
  public static PetrismatchmodelTurnTypeAssert assertThat(es.us.dp1.l6_3_24_25.Petris.match.model.TurnType actual) {
    return new PetrismatchmodelTurnTypeAssert(actual);
  }

  /**
   * Creates a new <code>{@link Assertions}</code>.
   */
  protected Assertions() {
    // empty
  }
}
