import sys
import os
from unittest import TestCase

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../../..')))

from matchpredictor.matchresults.result import Team, Fixture, Outcome, Result
from matchpredictor.predictors.advanced_predictor import AdvancedPredictor


class TestAdvancedPredictor(TestCase):
    def setUp(self):
        training_data = [
            Result(fixture=Fixture(home_team=Team('Team A'), away_team=Team('Team B'), league='some league'),
                   outcome=Outcome.HOME, home_goals=2, away_goals=1, season=2020),
            Result(fixture=Fixture(home_team=Team('Team C'), away_team=Team('Team A'), league='some league'),
                   outcome=Outcome.AWAY, home_goals=0, away_goals=3, season=2020),
            Result(fixture=Fixture(home_team=Team('Team B'), away_team=Team('Team C'), league='some league'),
                   outcome=Outcome.DRAW, home_goals=1, away_goals=1, season=2020),
        ]
        self.predictor = AdvancedPredictor(training_data)

    def test_predict(self):
        fixture = Fixture(home_team=Team('Team A'), away_team=Team('Team B'), league='some league')
        prediction = self.predictor.predict(fixture)
        self.assertIn(prediction.outcome, [Outcome.HOME, Outcome.AWAY, Outcome.DRAW])

    def test_accuracy(self):
        matches = [
            Fixture(home_team=Team('Team A'), away_team=Team('Team B'), league='some league'),
            Fixture(home_team=Team('Team C'), away_team=Team('Team A'), league='some league'),
            Fixture(home_team=Team('Team B'), away_team=Team('Team C'), league='some league'),
        ]
        results = [
            Outcome.HOME,
            Outcome.AWAY,
            Outcome.DRAW
        ]
        correct_predictions = 0
        for match, result in zip(matches, results):
            if self.predictor.predict(match).outcome == result:
                correct_predictions += 1
        accuracy = correct_predictions / len(matches)
        self.assertGreaterEqual(accuracy, 0.5)


if __name__ == '__main__':
    unittest.main()
