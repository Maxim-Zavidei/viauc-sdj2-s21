package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Message;
import model.Model;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApplicationViewModel implements PropertyChangeListener {

    private Model model;
    private StringProperty createChatField;
    private StringProperty joinChatField;
    private StringProperty messageField;
    private StringProperty errorLabel;
    private StringProperty selectedChat;
    private ObservableList<String> chatList;
    private Map<String, ArrayList<Message>> storedChats;
    private ObservableList<String> selectedChatMessages;

    public ApplicationViewModel(Model model) {
        this.model = model;
        createChatField = new SimpleStringProperty("");
        joinChatField = new SimpleStringProperty("");
        messageField = new SimpleStringProperty("");
        errorLabel = new SimpleStringProperty("");
        selectedChat = new SimpleStringProperty("");
        chatList = FXCollections.observableArrayList();
        storedChats = new HashMap<>();
        selectedChatMessages = FXCollections.observableArrayList();
        model.addListenerOfBroadcasts(this);
    }

    public void reset() {
        createChatField.set("");
        joinChatField.set("");
        messageField.set("");
        errorLabel.set("");
        selectedChat.set("");
    }

    public StringProperty getCreateChatFieldProperty() {
        return createChatField;
    }

    public StringProperty getJoinChatFieldProperty() {
        return joinChatField;
    }

    public StringProperty getMessageFieldProperty() {
        return messageField;
    }

    public StringProperty getErrorLabel() {
        return errorLabel;
    }

    public StringProperty getSelectedChatProperty() {
        return selectedChat;
    }

    public ObservableList<String> getSelectedChatMessages() {
        return selectedChatMessages;
    }

    public ObservableList<String> getChatList() {
        return chatList;
    }

    public void createChat() {
        try {
            model.createChat(createChatField.get());
            createChatField.set("");
        } catch (Exception e) {
            errorLabel.set(e.getMessage());
        }
    }

    public void joinChat() {
        try {
            model.joinChat(joinChatField.get());
            joinChatField.set("");
        } catch (Exception e) {
            errorLabel.set(e.getMessage());
        }
    }

    public void sendMessage() {
        try {
            model.sendMessage(selectedChat.get(), messageField.get());
            messageField.set("");
        } catch (Exception e) {
            errorLabel.set(e.getMessage());
        }
    }

    public void refreshMessages(String uuid) {
        if (selectedChat.get() == null) return;
        selectedChatMessages.clear();
        if (uuid.isEmpty()) uuid = selectedChat.get();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (Message message : storedChats.get(uuid)) {
            selectedChatMessages.add("[" + sdf.format(message.getDate()) + "] " + message.getSender() + ": " + message.getMessage());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            switch (evt.getPropertyName()) {
                case "message" : {
                    String uuid = evt.getOldValue().toString();
                    if (storedChats.get(uuid) == null) {
                        storedChats.put(uuid, new ArrayList<>());
                        chatList.add(uuid);
                    }
                    storedChats.get(uuid).add((Message) evt.getNewValue());
                    if (selectedChat.get() != null && selectedChat.get().equals(uuid)) refreshMessages(uuid);
                }
            }
        });
    }
}
