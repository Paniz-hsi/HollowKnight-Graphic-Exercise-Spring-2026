package D.HollowKnight.models;

import java.sql.*;

public class DatabaseManager {
    private Connection conn;

    public DatabaseManager() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:savegame.db");
            createTables();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS settings (" +
                "id INTEGER PRIMARY KEY, volume INTEGER, music_on BOOLEAN, " +
                "sfx_on BOOLEAN, brightness INTEGER, language TEXT, " +
                "key_up INTEGER, key_down INTEGER, key_left INTEGER, key_right INTEGER, " +
                "key_dash INTEGER, key_attack INTEGER, key_jump INTEGER)");

            stmt.execute("CREATE TABLE IF NOT EXISTS saves (" +
                "slot INTEGER PRIMARY KEY, has_save BOOLEAN, " +
                "map_name TEXT, progress INTEGER)");
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM settings")) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (Statement insertStmt = conn.createStatement()) {
                    insertStmt.execute("INSERT INTO settings (id, volume, music_on, sfx_on, brightness, language, " +
                        "key_up, key_down, key_left, key_right, key_dash, key_attack, key_jump) " +
                        "VALUES (1, 100, 1, 1, 50, 'en', 19, 20, 21, 22, 31, 52, 54)");
                }
            }
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rsSaves = stmt.executeQuery("SELECT COUNT(*) FROM saves")) {

            if (rsSaves.next() && rsSaves.getInt(1) == 0) {
                try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO saves (slot, has_save, map_name, progress) VALUES (?, 0, 'UNKNOWN', 0)")) {
                    for (int i = 1; i <= 4; i++) {
                        pstmt.setInt(1, i);
                        pstmt.executeUpdate();
                    }
                }
            }
        }
    }

    public int getVolume() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT volume FROM settings WHERE id = 1")) {
            return rs.getInt("volume");
        } catch (SQLException e) { return 100; }
    }

    public void updateVolume(int volume) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET volume = ? WHERE id = 1")) {
            pstmt.setInt(1, volume); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean isMusicOn() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT music_on FROM settings WHERE id = 1")) {
            return rs.getBoolean("music_on");
        } catch (SQLException e) { return true; }
    }

    public void updateMusicOn(boolean isOn) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET music_on = ? WHERE id = 1")) {
            pstmt.setBoolean(1, isOn); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean isSfxOn() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT sfx_on FROM settings WHERE id = 1")) {
            return rs.getBoolean("sfx_on");
        } catch (SQLException e) { return true; }
    }

    public void updateSfxOn(boolean isOn) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET sfx_on = ? WHERE id = 1")) {
            pstmt.setBoolean(1, isOn); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getBrightness() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT brightness FROM settings WHERE id = 1")) {
            return rs.getInt("brightness");
        } catch (SQLException e) { return 100; }
    }

    public void updateBrightness(int brightness) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET brightness = ? WHERE id = 1")) {
            pstmt.setInt(1, brightness); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public String getLanguage() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT language FROM settings WHERE id = 1")) {
            return rs.getString("language");
        } catch (SQLException e) { return "ENGLISH"; }
    }

    public void updateLanguage(String language) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET language = ? WHERE id = 1")) {
            pstmt.setString(1, language); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public SaveSlotData getSaveSlot(int slotNumber) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM saves WHERE slot = ?")) {
            pstmt.setInt(1, slotNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new SaveSlotData(rs.getBoolean("has_save"), rs.getString("map_name"), rs.getInt("progress"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new SaveSlotData(false, "", 0);
    }

    public int getKeyUp() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT key_up FROM settings WHERE id = 1")) {
            return rs.next() ? rs.getInt("key_up") : 19; // 19 = Keys.UP
        } catch (SQLException e) {
            return 19;
        }
    }

    public void updateKeyUp(int keycode) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET key_up = ? WHERE id = 1")) {
            pstmt.setInt(1, keycode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKeyDown() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT key_down FROM settings WHERE id = 1")) {
            return rs.next() ? rs.getInt("key_down") : 20; // 20 = Keys.DOWN
        } catch (SQLException e) {
            return 20;
        }
    }

    public void updateKeyDown(int keycode) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET key_down = ? WHERE id = 1")) {
            pstmt.setInt(1, keycode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKeyLeft() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT key_left FROM settings WHERE id = 1")) {
            return rs.next() ? rs.getInt("key_left") : 21; // 21 = Keys.LEFT
        } catch (SQLException e) {
            return 21;
        }
    }

    public void updateKeyLeft(int keycode) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET key_left = ? WHERE id = 1")) {
            pstmt.setInt(1, keycode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKeyRight() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT key_right FROM settings WHERE id = 1")) {
            return rs.next() ? rs.getInt("key_right") : 22; // 22 = Keys.RIGHT
        } catch (SQLException e) {
            return 22;
        }
    }

    public void updateKeyRight(int keycode) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET key_right = ? WHERE id = 1")) {
            pstmt.setInt(1, keycode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int getKeyDash() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT key_dash FROM settings WHERE id = 1")) {
            return rs.next() ? rs.getInt("key_dash") : 31; // 31 = Keys.C
        } catch (SQLException e) { return 31; }
    }

    public void updateKeyDash(int keycode) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET key_dash = ? WHERE id = 1")) {
            pstmt.setInt(1, keycode); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getKeyAttack() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT key_attack FROM settings WHERE id = 1")) {
            return rs.next() ? rs.getInt("key_attack") : 52; // 52 = Keys.X
        } catch (SQLException e) { return 52; }
    }

    public void updateKeyAttack(int keycode) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET key_attack = ? WHERE id = 1")) {
            pstmt.setInt(1, keycode); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getKeyJump() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT key_jump FROM settings WHERE id = 1")) {
            return rs.next() ? rs.getInt("key_jump") : 54; // 54 = Keys.Z
        } catch (SQLException e) { return 54; }
    }

    public void updateKeyJump(int keycode) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET key_jump = ? WHERE id = 1")) {
            pstmt.setInt(1, keycode); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
