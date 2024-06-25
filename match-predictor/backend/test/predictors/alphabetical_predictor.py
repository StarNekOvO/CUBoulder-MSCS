from matchpredictor.predictors.predictor import Predictor
from matchpredictor.matchresults.result import Team, Fixture, Outcome


class AlphabeticalPredictor(Predictor):
    def predict(self, fixture: Fixture) -> Outcome:
        # 按字母顺序选择第一个球队为获胜者
        teams = sorted([fixture.home_team.name, fixture.away_team.name])
        return Outcome.HOME if teams[0] == fixture.home_team.name else Outcome.AWAY
