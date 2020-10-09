package ${PACKAGE_NAME};

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import de.itsTyrion.flusenbot.command.Command;
import de.itsTyrion.flusenbot.command.CommandInfo;

@CommandInfo(name = "${command_name}")
public class ${NAME} extends Command {

    @Override
    protected boolean execute(String[] args, Chat chat, User user) {
        return true;
    }
}