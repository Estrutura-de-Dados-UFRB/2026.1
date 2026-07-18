package util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class SoundPlayer {
    private static Clip clipMusica;
    private static float volumeGlobal = 0.2f;
    
    // Cache para sons curtos (efeitos sonoros)
    private static final Map<String, Clip> cacheSons = new HashMap<>();

    /**
     * Carrega o arquivo de áudio para a memória RAM.
     * Use isso no início do jogo para evitar delays.
     */
    public static void preCarregar(String nomeArquivo) {
        try {
            if (cacheSons.containsKey(nomeArquivo)) return;

            InputStream is = ResourceLoader.getResourceAsStream("/sounds/" + nomeArquivo);
            if (is == null) {
                System.err.println("Arquivo não encontrado em /sounds/: " + nomeArquivo);
                return;
            }
            
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            
            cacheSons.put(nomeArquivo, clip);
        } catch (Exception e) {
            System.err.println("Erro ao pré-carregar som: " + nomeArquivo);
        }
    }

    /**
     * Toca um som. Se for a música de Pokémon, aplica o loop especial.
     */
    public static void tocar(String nomeArquivo, boolean loop) {
        try {
            // Lógica especial para a música de fundo
            if (nomeArquivo.equals("pokemon.wav")) {
                tocarMusicaComLoopEspecial(nomeArquivo, 8);
                return;
            }

            // Para efeitos sonoros, busca no cache
            Clip clip = cacheSons.get(nomeArquivo);
            
            if (clip == null) {
                preCarregar(nomeArquivo);
                clip = cacheSons.get(nomeArquivo);
            }

            if (clip != null) {
                if (clip.isRunning()) clip.stop(); 
                clip.setFramePosition(0); 
                setVolumeClip(clip, volumeGlobal);
                
                if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("Erro ao tocar som: " + nomeArquivo);
        }
    }

    private static void tocarMusicaComLoopEspecial(String nomeArquivo, int segundosInicio) {
        try {
            pararMusica(); // Garante que não toque duas músicas ao mesmo tempo
            
            InputStream is = ResourceLoader.getResourceAsStream("/sounds/" + nomeArquivo);
            if (is == null) return;
            
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            clipMusica = AudioSystem.getClip();
            clipMusica.open(ais);
            setVolumeClip(clipMusica, volumeGlobal);

            // Listener para detectar o fim da música e aplicar o loop a partir dos 8s
            clipMusica.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && clipMusica != null) {
                    // Verifica se a música parou porque chegou ao fim
                    if (clipMusica.getFramePosition() >= clipMusica.getFrameLength()) {
                        long frameInicio = (long) (segundosInicio * clipMusica.getFormat().getFrameRate());
                        clipMusica.setFramePosition((int) frameInicio);
                        clipMusica.start();
                    }
                }
            });
            clipMusica.start();
        } catch (Exception e) {
            System.err.println("Erro na música de fundo: " + e.getMessage());
        }
    }

    /**
     * Para TUDO (música e efeitos). Essencial para o Novo Jogo.
     */
    public static void parar() {
        pararMusica();
        for (Clip c : cacheSons.values()) {
            if (c.isRunning()) c.stop();
        }
    }

    public static void pararMusica() {
        if (clipMusica != null) {
            clipMusica.stop();
            clipMusica.close();
            clipMusica = null;
        }
    }

    public static void setVolumeGlobal(float volume) {
        volumeGlobal = volume;
        if (clipMusica != null) setVolumeClip(clipMusica, volume);
        for (Clip c : cacheSons.values()) setVolumeClip(c, volume);
    }

    private static void setVolumeClip(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume <= 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    public static float getVolumeGlobal() {
        return volumeGlobal; 
    }
}
