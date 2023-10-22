package w.commander;

import lombok.extern.slf4j.Slf4j;

/**
 * @author whilein
 */
@Slf4j
public class TestCommandSender implements CommandSender {
    @Override
    public String name() {
        return "test";
    }

    @Override
    public void sendMessage(String text) {
        log.info(text);
    }
}
