/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.conditionwatcher;

/**
 * Created by F1sherKK on 08/10/15.
 */
public class ConditionWatcher {

  public static final int CONDITION_NOT_MET = 0;
  public static final int CONDITION_MET = 1;
  public static final int TIMEOUT = 2;

  public static final int DEFAULT_TIMEOUT_LIMIT = 1000 * 60;
  public static final int DEFAULT_INTERVAL = 250;
  private static ConditionWatcher conditionWatcher;
  private int timeoutLimit = DEFAULT_TIMEOUT_LIMIT;
  private int watchInterval = DEFAULT_INTERVAL;

  private ConditionWatcher() {
    super();
  }

  public static ConditionWatcher getInstance() {
    if (conditionWatcher == null) {
      conditionWatcher = new ConditionWatcher();
    }
    return conditionWatcher;
  }

  public static void waitForCondition(int timeoutLimit, Instruction instruction) throws Exception {
    setTimeoutLimit(timeoutLimit);
    waitForCondition(instruction);
  }

  public static void waitForCondition(Instruction instruction) throws Exception {
    int status = CONDITION_NOT_MET;
    int elapsedTime = 0;

    do {
      if (instruction.checkCondition()) {
        status = CONDITION_MET;
      } else {
        elapsedTime += getInstance().watchInterval;
        Thread.sleep(getInstance().watchInterval);
      }

      if (elapsedTime >= getInstance().timeoutLimit) {
        status = TIMEOUT;
        break;
      }
    } while (status != CONDITION_MET);

    if (status == TIMEOUT)
      throw new Exception(instruction.getDescription() + " - took more than " + getInstance().timeoutLimit / 1000 + " seconds. Test stopped.");
  }

  public static void setWatchInterval(int watchInterval) {
    getInstance().watchInterval = watchInterval;
  }

  public static void setTimeoutLimit(int ms) {
    getInstance().timeoutLimit = ms;
  }
}
