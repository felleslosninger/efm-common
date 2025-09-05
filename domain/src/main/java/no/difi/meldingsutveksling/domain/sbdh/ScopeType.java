package no.difi.meldingsutveksling.domain.sbdh;

public enum ScopeType {
    JOURNALPOST_ID("JournalpostId"),
    CONVERSATION_ID("ConversationId"),
    SENDER_REF("SenderRef"),
    RECEIVER_REF("ReceiverRef"),
    MESSAGE_CHANNEL("MessageChannel"),
    SENDER_HERID1("SenderHerId1"),
    SENDER_HERID2("SenderHerId2"),
    RECEIVER_HERID1("ReceiverHerId1"),
    RECEIVER_HERID2("ReceiverHerId2");

    private final String fullname;

    ScopeType(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    @Override
    public String toString() {
        return this.fullname;
    }
}
