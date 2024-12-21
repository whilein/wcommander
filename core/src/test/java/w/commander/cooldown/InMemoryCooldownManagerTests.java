package w.commander.cooldown;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import w.commander.TestCommandActor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author whilein
 */
class InMemoryCooldownManagerTests {

    InMemoryCooldownManager cooldownManager;

    Map<String, InMemoryCooldownManager.Expirations> map;

    @BeforeEach
    void setup() {
        cooldownManager = new InMemoryCooldownManager("test", map = new HashMap<>());
    }

    @AfterEach
    void clearAllCaches() {
        Mockito.clearAllCaches();
    }

    @Test
    void checkCooldownAfterSet() {
        val actor1 = new TestCommandActor("foo");
        val actor2 = new TestCommandActor("bar");

        val action = "dick_suck";

        // у обоих пользователей не должно быть КД
        assertFalse(cooldownManager.hasCooldown(actor1, action));
        assertFalse(cooldownManager.hasCooldown(actor2, action));
        assertNull(cooldownManager.getCooldown(actor1, action));
        assertNull(cooldownManager.getCooldown(actor2, action));

        // ставим первому пользователю КД
        cooldownManager.setCooldown(actor1, action, Duration.ofMinutes(5));
        assertTrue(cooldownManager.hasCooldown(actor1, action));

        // проверяем первого пользователя
        Duration actualCooldown = cooldownManager.getCooldown(actor1, action);
        assertNotNull(actualCooldown);
        assertEquals(5 * 60, actualCooldown.getSeconds(), 1);

        // второй пользователь не должен быть затронут
        assertFalse(cooldownManager.hasCooldown(actor2, action));
        assertNull(cooldownManager.getCooldown(actor2, action));

        // ставим второму пользователю КД
        cooldownManager.setCooldown(actor2, action, Duration.ofMinutes(5));
        assertTrue(cooldownManager.hasCooldown(actor2, action));

        // проверяем второго пользователя
        actualCooldown = cooldownManager.getCooldown(actor2, action);
        assertNotNull(actualCooldown);
        assertEquals(5 * 60, actualCooldown.getSeconds(), 1);
    }

    @Test
    void checkCooldownAfterExpire() {
        val actor = new TestCommandActor("foo");
        val action = "dick_suck";

        // ставим КД
        cooldownManager.setCooldown(actor, action, Duration.ofMillis(100));
        assertTrue(cooldownManager.hasCooldown(actor, action));

        // ждем истечение КД
        assertDoesNotThrow(() -> Thread.sleep(150));

        // КД должно было истечь
        assertFalse(cooldownManager.hasCooldown(actor, action));
        assertNull(cooldownManager.getCooldown(actor, action));
    }

    @Test
    void cleanupAfterExpire() {
        val actor1 = new TestCommandActor("foo");
        val actor2 = new TestCommandActor("bar");

        val action1 = "dick_suck";
        val action2 = "pussy_lick";

        // ставим КД первому пользователю
        cooldownManager.setCooldown(actor1, action1, Duration.ofMillis(100));
        assertTrue(cooldownManager.hasCooldown(actor1, action1));

        // ждем истечение КД
        assertDoesNotThrow(() -> Thread.sleep(150));

        // КД должно было остаться в Map
        assertTrue(map.containsKey(action1));
        assertFalse(map.containsKey(action2));

        // ставим КД второму пользователю
        cooldownManager.setCooldown(actor2, action2, Duration.ofMillis(100));

        // первое КД должно было удалиться, поскольку setCooldown чистит КД
        assertFalse(map.containsKey(action1));

        // второе КД должно было добавиться
        assertTrue(map.containsKey(action2));
    }

}
