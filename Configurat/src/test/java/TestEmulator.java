import org.example.first.CommandHandler;
import org.example.first.Emulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestEmulator {

    private static Emulator emulator;
    private static CommandHandler commandHandler;

    @BeforeAll
    public static void setUp() {
        try {
            emulator = new Emulator("C:\\Users\\nazar\\OneDrive\\Desktop\\study\\Configurat\\src\\main\\resources\\config.json");
            commandHandler=emulator.getCommandHandler();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLsCd() {
        commandHandler.executeCommand("cd Fs");
        commandHandler.executeCommand("ls");
    }
    @Test
    public void testLsCd2() {
        commandHandler.executeCommand("cd ");
    }

}
