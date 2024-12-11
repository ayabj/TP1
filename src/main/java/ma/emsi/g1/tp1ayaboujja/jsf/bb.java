package ma.emsi.g1.tp1ayaboujja.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.g1.tp1ayaboujja.llm.JsonUtilPourGemini;
import ma.emsi.g1.tp1ayaboujja.llm.LlmInteraction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Backing bean pour la page JSF index.xhtml.
 * Portée view pour conserver l'état de la conversation pendant plusieurs requêtes HTTP.
 */
@Named
@ViewScoped
public class bb implements Serializable {

    private String systemRole = "helpful assistant";
    private boolean systemRoleChangeable = true;
    private String question;
    private String reponse;
    private boolean debug;
    private String texteRequeteJson;
    private String texteReponseJson;
    private StringBuilder conversation = new StringBuilder();

    @Inject
    private FacesContext facesContext;

    @Inject
    private JsonUtilPourGemini jsonUtil; // Injection de la classe utilitaire

    public bb() {
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getTexteRequeteJson() {
        return texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }

    public String getTexteReponseJson() {
        return texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }

    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    public boolean isSystemRoleChangeable() {
        return systemRoleChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }
    public void toggleDebug() {
        this.setDebug(!isDebug());
    }


    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }

        try {
            // Appel à la méthode envoyerRequete pour interagir avec l'API LLM
            LlmInteraction interaction = jsonUtil.envoyerRequete(question);
            this.reponse = interaction.reponseExtraite(); // Extrait la réponse
            this.texteRequeteJson = interaction.texteRequeteJson(); // JSON de la requête
            this.texteReponseJson = interaction.texteReponseJson(); // JSON de la réponse

            // Affiche la conversation mise à jour dans le textarea

        } catch (Exception e) {
            // Gestion des erreurs avec affichage du message
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API du LLM",
                    "Problème de connexion avec l'API du LLM : " + e.getMessage());
            facesContext.addMessage(null, message);
        }
        afficherConversation();
        return null; // Rester sur la même page
    }


    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
    }

    public List<SelectItem> getSystemRoles() {
        List<SelectItem> listeSystemRoles = new ArrayList<>();
        String role = """
                You are a helpful assistant. You help the user to find the information they need.
                If the user type a question, you answer it.
                """;
        listeSystemRoles.add(new SelectItem(role, "Assistant"));
        role = """
                You are an interpreter. You translate from English to French and from French to English.
                If the user type a French text, you translate it into English.
                If the user type an English text, you translate it into French.
                If the text contains only one to three words, give some examples of usage of these words in English.
                """;
        listeSystemRoles.add(new SelectItem(role, "Traducteur Anglais-Français"));
        role = """
                Your are a travel guide. If the user type the name of a country or of a town,
                you tell them what are the main places to visit in the country or the town
                are you tell them the average price of a meal.
                """;
        listeSystemRoles.add(new SelectItem(role, "Guide touristique"));
        return listeSystemRoles;
    }

    public String nouveauChat() {
        return "index";
    }
}