import random

from matchpredictor.predictors.predictor import Predictor, Prediction
from matchpredictor.matchresults.result import Fixture, Outcome


class AdvancedPredictor(Predictor):
    def __init__(self, training_data):
        self.training_data = training_data
        self.team_strengths = self.calculate_team_strengths(training_data)

    def calculate_team_strengths(self, training_data):
        strengths = {}
        for result in training_data:
            if result.fixture.home_team.name not in strengths:
                strengths[result.fixture.home_team.name] = 0
            if result.fixture.away_team.name not in strengths:
                strengths[result.fixture.away_team.name] = 0

            if result.outcome == Outcome.HOME:
                strengths[result.fixture.home_team.name] += 3
            elif result.outcome == Outcome.AWAY:
                strengths[result.fixture.away_team.name] += 3
            else:
                strengths[result.fixture.home_team.name] += 1
                strengths[result.fixture.away_team.name] += 1

        return strengths

    def predict(self, fixture: Fixture) -> Prediction:
        home_strength = self.team_strengths.get(fixture.home_team.name, 0)
        away_strength = self.team_strengths.get(fixture.away_team.name, 0)

        if home_strength > away_strength:
            return Prediction(outcome=Outcome.HOME, confidence=0.6)
        elif away_strength > home_strength:
            return Prediction(outcome=Outcome.AWAY, confidence=0.6)
        else:
            return Prediction(outcome=Outcome.DRAW, confidence=0.4)
