package com.felipecsl.elifut;

import com.felipecsl.elifut.models.Club;
import com.felipecsl.elifut.models.Goal;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultMatchStatisticsTest {
  private Club home = Club.create(0, "Gremio", "", "", 0);
  private Club away = Club.create(1, "Internacional", "", "", 0);
  private RandomGenerator randomMock = mock(RandomGenerator.class);
  private RealDistribution goalsDistribution = mock(RealDistribution.class);

  @Test public void testSimpleHomeWin() {
    when(randomMock.nextFloat()).thenReturn(MatchStatistics.HOME_WIN_PROBABILITY);
    when(goalsDistribution.sample()).thenReturn(1.0);

    DefaultMatchStatistics stats =
        new DefaultMatchStatistics(home, away, randomMock, goalsDistribution);

    assertThat(stats.home()).isEqualTo(home);
    assertThat(stats.away()).isEqualTo(away);
    assertThat(stats.winner()).isEqualTo(home);
    assertThat(stats.loser()).isEqualTo(away);
    assertThat(stats.finalScore()).isEqualTo("1x0");
    assertThat(stats.isDraw()).isEqualTo(false);
    assertThat(stats.homeGoals().size()).isEqualTo(1);
    assertThat(stats.awayGoals().size()).isEqualTo(0);
  }

  @Test public void testSimpleDraw() {
    when(randomMock.nextFloat()).thenReturn(MatchStatistics.DRAW_PROBABILITY);
    when(goalsDistribution.sample()).thenReturn(2.0);

    DefaultMatchStatistics stats =
        new DefaultMatchStatistics(home, away, randomMock, goalsDistribution);

    assertThat(stats.home()).isEqualTo(home);
    assertThat(stats.away()).isEqualTo(away);
    assertThat(stats.winner()).isEqualTo(null);
    assertThat(stats.loser()).isEqualTo(null);
    assertThat(stats.finalScore()).isEqualTo("1x1");
    assertThat(stats.isDraw()).isEqualTo(true);
    assertThat(stats.homeGoals().size()).isEqualTo(1);
    assertThat(stats.awayGoals().size()).isEqualTo(1);
  }

  @Test public void testSimpleAwayWin() {
    when(randomMock.nextFloat()).thenReturn(MatchStatistics.DRAW_PROBABILITY + 0.1f);
    when(goalsDistribution.sample()).thenReturn(1.0);

    DefaultMatchStatistics stats =
        new DefaultMatchStatistics(home, away, randomMock, goalsDistribution);

    assertThat(stats.home()).isEqualTo(home);
    assertThat(stats.away()).isEqualTo(away);
    assertThat(stats.winner()).isEqualTo(away);
    assertThat(stats.loser()).isEqualTo(home);
    assertThat(stats.finalScore()).isEqualTo("0x1");
    assertThat(stats.isDraw()).isEqualTo(false);
    assertThat(stats.homeGoals().size()).isEqualTo(0);
    assertThat(stats.awayGoals().size()).isEqualTo(1);
  }

  @Test public void testEventsAtTime() {
    when(randomMock.nextInt(90)).thenReturn(30).thenReturn(35);
    when(randomMock.nextFloat()).thenReturn(MatchStatistics.DRAW_PROBABILITY + 0.1f);
    when(goalsDistribution.sample()).thenReturn(2.0);

    DefaultMatchStatistics stats =
        new DefaultMatchStatistics(home, away, randomMock, goalsDistribution);

    assertThat(stats.eventsAtTime(30)).isEqualTo(Collections.singleton(Goal.create(30, away)));
    assertThat(stats.eventsAtTime(35)).isEqualTo(Collections.singleton(Goal.create(35, away)));
    assertThat(stats.eventsAtTime(40)).isEqualTo(Collections.emptySet());
  }
}
