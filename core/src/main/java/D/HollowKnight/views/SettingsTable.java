package D.HollowKnight.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SettingsTable extends Table {

    private boolean isMusicOn = true;
    private boolean isSfxOn = true;
    private String currentLang = "ENGLISH";

    public SettingsTable(TextButton.TextButtonStyle btnStyle, Table mainTable, Image settingsBg) {
        this.setFillParent(true);
        this.center().padTop(100);
        this.setPosition(1280, 0);

        Pixmap bgPixmap = new Pixmap(300, 10, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.DARK_GRAY);
        bgPixmap.fill();
        TextureRegionDrawable sliderBg = new TextureRegionDrawable(new Texture(bgPixmap));
        bgPixmap.dispose();

        Pixmap knobPixmap = new Pixmap(20, 30, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(Color.WHITE);
        knobPixmap.fill();
        TextureRegionDrawable sliderKnob = new TextureRegionDrawable(new Texture(knobPixmap));
        knobPixmap.dispose();

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(sliderBg, sliderKnob);
        Label.LabelStyle labelStyle = new Label.LabelStyle(btnStyle.font, Color.WHITE);

        Label volumeLabel = new Label("MUSIC VOLUME : 50%", labelStyle);
        Slider volumeSlider = new Slider(0, 100, 1, false, sliderStyle);
        volumeSlider.setValue(50);

        TextButton toggleMusicBtn = new TextButton("MUSIC : ON", btnStyle);
        TextButton toggleSfxBtn = new TextButton("SFX : ON", btnStyle);
        TextButton resetSoundsBtn = new TextButton("RESET SOUNDS", btnStyle);

        TextButton controlsBtn = new TextButton("CHANGE CONTROLS", btnStyle);
        TextButton resetControlsBtn = new TextButton("RESET CONTROLS", btnStyle);

        Label brightnessLabel = new Label("BRIGHTNESS : 100%", labelStyle);
        Slider brightnessSlider = new Slider(20, 100, 1, false, sliderStyle);
        brightnessSlider.setValue(100);

        TextButton langBtn = new TextButton("LANGUAGE : " + currentLang, btnStyle);
        TextButton backBtn = new TextButton("BACK", btnStyle);

        this.add(volumeLabel).padBottom(5).padLeft(45).row();
        this.add(volumeSlider).width(300).padBottom(20).padLeft(45).row();

        this.add(toggleMusicBtn).padBottom(10).padLeft(45).row();
        this.add(toggleSfxBtn).padBottom(10).padLeft(45).row();
        this.add(resetSoundsBtn).padBottom(20).padLeft(45).row();

        this.add(controlsBtn).padBottom(10).padLeft(45).row();
        this.add(resetControlsBtn).padBottom(20).padLeft(45).row();

        this.add(brightnessLabel).padBottom(5).padLeft(45).row();
        this.add(brightnessSlider).width(300).padBottom(20).padLeft(45).row();

        this.add(langBtn).padBottom(20).padLeft(45).row();
        this.add(backBtn).padLeft(45).row();

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int val = (int) volumeSlider.getValue();
                volumeLabel.setText("MUSIC VOLUME : " + val + "%");
                //TODO logic for setting music volume
            }
        });

        brightnessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int val = (int) brightnessSlider.getValue();
                brightnessLabel.setText("BRIGHTNESS : " + val + "%");
                //TODO logic for setting brightness level
            }
        });

        toggleMusicBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                isMusicOn = !isMusicOn;
                toggleMusicBtn.setText(isMusicOn ? "MUSIC : ON" : "MUSIC : OFF");
            }
        });

        toggleSfxBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                isSfxOn = !isSfxOn;
                toggleSfxBtn.setText(isSfxOn ? "SFX : ON" : "SFX : OFF");
            }
        });

        resetSoundsBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                volumeSlider.setValue(100);
                isMusicOn = true;
                isSfxOn = true;
                toggleMusicBtn.setText("MUSIC : ON");
                toggleSfxBtn.setText("SFX : ON");
            }
        });

        langBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                currentLang = currentLang.equals("ENGLISH") ? "PERSIAN" : "ENGLISH";
                langBtn.setText("LANGUAGE : " + currentLang);
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsTable.this.addAction(Actions.moveTo(1280, 0, 0.5f, Interpolation.exp10Out));
                mainTable.addAction(Actions.moveTo(0, 0, 0.5f, Interpolation.exp10Out));
                settingsBg.addAction(Actions.fadeOut(0.5f));
            }
        });
    }
}
