package D.HollowKnight.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Zote {
    private Vector2 position;
    private Rectangle interactBounds;
    private final float INTERACT_RANGE = 2f;

    private boolean isTalking = false;

    public enum DialogueState { MAIN_STORY, PRECEPTS }
    private DialogueState currentDialogueState = DialogueState.MAIN_STORY;
    private boolean isDialogueActive = false;

    private String[] mainDialogues;
    private String[] precepts;
    private int currentDialogueIndex = 0;
    private int currentPreceptIndex = 0;

    private String currentFullText = "";
    private String displayedText = "";
    private float typewriterTimer = 0f;
    private final float CHARS_PER_SECOND = 40f;
    private boolean isPlayerNear = false;
    private String[] zoteVoices = {
        "Zote_01.wav",
        "Zote_02.wav",
        "Zote_03.wav",
        "Zote_04.wav",
        "Zote_05.wav"
    };
    private Random random = new Random();

    public Zote(float x, float y) {
        this.position = new Vector2(x, y);
        this.interactBounds = new Rectangle(x - INTERACT_RANGE/2, y - INTERACT_RANGE/2, INTERACT_RANGE * 2, INTERACT_RANGE * 2);

        initDialogues();
    }

    private void initDialogues() {
        mainDialogues = new String[] {
            "Hmph! What are you staring at,\nyou wretched beast?",
            "I am Zote the Mighty,\na knight of great renown!",
            "Cross my path again,\nand you shall taste my deadly nail,\nLife Ender!"
        };

        precepts = new String[] {
            "Precept One: Always Win Your Battles.\nIn battle, winning is everything.",
            "Precept Two: Never Let Them Laugh\nat You. Fools laugh at everything.\nDo not tolerate it.",
            "Precept Three: Always Be Rested.\nFighting and adventuring\ntake their toll.\nRest when you can.",
            "Precept Four: Forget Your Past.\nThe past is painful,\nand thinking about it\nonly brings misery.",
            "Precept Five: Strength Beats Strength.\nIs your opponent strong?\nSimply overcome them with\ngreater strength!"
        };
    }

    public void update(float delta, Player player) {
        isPlayerNear = interactBounds.contains(player.getPosition().x, player.getPosition().y);

        if (isPlayerNear && (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) {
            if (!isDialogueActive) {
                startDialogue(player);
            } else {
                advanceDialogue(player);
            }
        }

        if (isDialogueActive && displayedText.length() < currentFullText.length()) {
            typewriterTimer += delta;
            int charsToShow = (int) (typewriterTimer * CHARS_PER_SECOND);
            charsToShow = Math.min(charsToShow, currentFullText.length());
            displayedText = currentFullText.substring(0, charsToShow);
        }
    }

    private void startDialogue(Player player) {
        isDialogueActive = true;
        isTalking = true;
        player.setCanMove(false);

        loadCurrentText();
        playZoteVoiceSFX();
    }

    private void advanceDialogue(Player player) {
        if (displayedText.length() < currentFullText.length()) {
            displayedText = currentFullText;
            return;
        }

        if (currentDialogueState == DialogueState.MAIN_STORY) {
            currentDialogueIndex++;
            if (currentDialogueIndex >= mainDialogues.length) {
                endDialogue(player);
                currentDialogueState = DialogueState.PRECEPTS;
                return;
            }
        } else {
            currentPreceptIndex++;
            if (currentPreceptIndex >= precepts.length) {
                currentPreceptIndex = 0;
            }
            endDialogue(player);
            return;
        }

        loadCurrentText();
        playZoteVoiceSFX();
    }

    private void loadCurrentText() {
        typewriterTimer = 0f;
        displayedText = "";
        if (currentDialogueState == DialogueState.MAIN_STORY) {
            currentFullText = mainDialogues[currentDialogueIndex];
        } else {
            currentFullText = precepts[currentPreceptIndex];
        }
    }

    private void endDialogue(Player player) {
        isDialogueActive = false;
        isTalking = false;
        player.setCanMove(true);
    }

    public boolean isDialogueActive() { return isDialogueActive; }
    public boolean isTalking() { return isTalking; }
    public String getDisplayedText() { return displayedText; }
    public Vector2 getPosition() { return position; }
    public boolean isPlayerNear() {
        return isPlayerNear;
    }
    private void playZoteVoiceSFX() {
        int index = random.nextInt(zoteVoices.length);
        String soundFile = zoteVoices[index];

        AudioManager.getInstance().playSound(soundFile);

        System.out.println("SFX: Played " + soundFile);
    }
}
