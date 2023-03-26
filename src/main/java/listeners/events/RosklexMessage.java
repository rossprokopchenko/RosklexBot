package listeners.events;


import boot.Rosklex;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class RosklexMessage {
    public static boolean isRosklexMessage(Message message) {
        List<Message.Attachment> attachments = message.getAttachments();

        if (attachments == null) return true;
        if (!attachments.isEmpty()) return false;

        Member member = message.getMember();
        String[] messageContent = message.getContentRaw().split(" ");

        if (member.getUser().isBot() || messageContent[0].charAt(0) != Rosklex.getPrefix()) {
            return false;
        }

        return true;
    }
}
