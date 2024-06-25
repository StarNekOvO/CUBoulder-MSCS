from unittest import TestCase

from matchpredictor.matchresults.result import Team, Fixture, Outcome
from matchpredictor.predictors.alphabetical_predictor import AlphabeticalPredictor


class TestAlphabeticalPredictor(TestCase):
    predictor = AlphabeticalPredictor()

    def test_predict(self) -> None:
        fixture = Fixture(home_team=Team('Alpha'), away_team=Team('Beta'), league='some league')
        prediction = self.predictor.predict(fixture)
        self.assertEqual(Outcome.HOME, prediction)

        fixture = Fixture(home_team=Team('Beta'), away_team=Team('Alpha'), league='some league')
        prediction = self.predictor.predict(fixture)
        self.assertEqual(Outcome.AWAY, prediction)

    def test_accuracy(self) -> None:
        matches = [
            Fixture(home_team=Team('Alpha'), away_team=Team('Beta'), league='some league'),
            Fixture(home_team=Team('Gamma'), away_team=Team('Alpha'), league='some league'),
            Fixture(home_team=Team('Beta'), away_team=Team('Gamma'), league='some league'),
        ]
        correct_predictions = 0
        results = [
            Outcome.HOME,
            Outcome.AWAY,
            Outcome.HOME
        ]
        for match, result in zip(matches, results):
            if self.predictor.predict(match) == result:
                correct_predictions += 1
        accuracy = correct_predictions / len(matches)
        self.assertGreaterEqual(accuracy, 0.33)
