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
                "map_name TEXT, progress INTEGER, spawn_point INTEGER, " +
                "current_masks INTEGER, max_masks INTEGER, soul INTEGER, unlocked_spawns TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS achievements (" +
                "name TEXT PRIMARY KEY, is_unlocked BOOLEAN)");
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
                String insertQuery = "INSERT INTO saves (slot, has_save, map_name, progress, spawn_point, current_masks, max_masks, soul, unlocked_spawns) VALUES (?, 0, 'UNKNOWN', 0, 1, 5, 5, 0, '1')";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    for (int i = 1; i <= 4; i++) {
                        pstmt.setInt(1, i);
                        pstmt.executeUpdate();
                    }
                }
            }
        }
        try (Statement checkStmt = conn.createStatement();
             ResultSet rsAch = checkStmt.executeQuery("SELECT COUNT(*) FROM achievements")) {

            if (rsAch.next() && rsAch.getInt(1) == 0) {
                String[] achievementsList = {
                    "Completion",
                    "Speedrun",
                    "True Hunter",
                    "Defeat False Knight",
                    "Resilient Knight"
                };
                String insertAchQuery = "INSERT INTO achievements (name, is_unlocked) VALUES (?, 0)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertAchQuery)) {
                    for (String ach : achievementsList) {
                        pstmt.setString(1, ach);
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

    public void saveGameState(int slot, String mapName, int spawnPointId, int progress, String unlockedSpawnsStr, int currentMasks, int maxMasks, int soul) {
        String sql = "UPDATE saves SET has_save = 1, map_name = ?, progress = ?, spawn_point = ?, unlocked_spawns = ?, current_masks = ?, max_masks = ?, soul = ? WHERE slot = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mapName);
            pstmt.setInt(2, progress);
            pstmt.setInt(3, spawnPointId);
            pstmt.setString(4, unlockedSpawnsStr);
            pstmt.setInt(5, currentMasks);
            pstmt.setInt(6, maxMasks);
            pstmt.setInt(7, soul);
            pstmt.setInt(8, slot);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSavedUnlockedSpawns(int slot) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT unlocked_spawns FROM saves WHERE slot = ?")) {
            pstmt.setInt(1, slot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("unlocked_spawns");
        } catch (SQLException e) { e.printStackTrace(); }
        return "1";
    }

    public int getSavedSpawnPoint(int slot) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT spawn_point FROM saves WHERE slot = ?")) {
            pstmt.setInt(1, slot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("spawn_point");
        } catch (SQLException e) { e.printStackTrace(); }
        return 1;
    }

    public int getSaveProgress(int slot) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT progress FROM saves WHERE slot = ?")) {
            pstmt.setInt(1, slot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("progress");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getSavedMasks(int slot) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT current_masks FROM saves WHERE slot = ?")) {
            pstmt.setInt(1, slot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("current_masks");
        } catch (SQLException e) { e.printStackTrace(); }
        return 5;
    }

    public int getSavedSoul(int slot) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT soul FROM saves WHERE slot = ?")) {
            pstmt.setInt(1, slot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("soul");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void deleteSaveSlot(int slot) {
        String sql = "UPDATE saves SET has_save = 0, map_name = 'UNKNOWN', progress = 0, spawn_point = 1, unlocked_spawns = '1', current_masks = 5, max_masks = 5, soul = 0 WHERE slot = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, slot);
            pstmt.executeUpdate();
            System.out.println("Slot " + slot + " deleted and reset to defaults.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isAchievementUnlocked(String name) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT is_unlocked FROM achievements WHERE name = ?")) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_unlocked");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void unlockAchievement(String name) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE achievements SET is_unlocked = 1 WHERE name = ?")) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Achievement Unlocked in DB: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
