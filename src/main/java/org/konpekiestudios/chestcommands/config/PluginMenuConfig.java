package org.konpekiestudios.chestcommands.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginMenuConfig {
    public int rows = 5;
    public int tables = 10;
    public Map<String, MenuDef> menus = new HashMap<>();

    public static class MenuDef {
        public String command;
        public String title;
        public Integer rows;
        public Map<String, ItemDef> items = new HashMap<>();
    }

    public static class ItemDef {
        public String material;
        public String name;
        public List<String> lore;
        public String action;
    }

    @SuppressWarnings("unchecked")
    public static PluginMenuConfig load(File dataFolder) throws Exception {
        File cfg = new File(dataFolder, "config.yml");
        if (!cfg.exists()) throw new IllegalStateException("config.yml não encontrado em " + dataFolder);
        Yaml yaml = new Yaml();
        try (FileInputStream in = new FileInputStream(cfg)) {
            Map<String, Object> root = yaml.load(in);
            PluginMenuConfig out = new PluginMenuConfig();

            Object chestCommands = root.get("ChestCommands");
            if (chestCommands instanceof Map) {
                Map<String, Object> cc = (Map<String, Object>) chestCommands;
                Object r = cc.get("rows");
                Object t = cc.get("tables");
                if (r instanceof Number) out.rows = ((Number) r).intValue();
                if (t instanceof Number) out.tables = ((Number) t).intValue();
            }

            Object menusObj = root.get("menus");
            if (menusObj instanceof Map) {
                Map<String, Object> menus = (Map<String, Object>) menusObj;
                for (Map.Entry<String, Object> e : menus.entrySet()) {
                    String key = e.getKey();
                    Object val = e.getValue();
                    if (!(val instanceof Map)) continue;
                    Map<String, Object> m = (Map<String, Object>) val;
                    MenuDef md = new MenuDef();
                    md.command = asString(m.get("command"));
                    md.title = asString(m.get("title"));
                    if (m.get("rows") instanceof Number) md.rows = ((Number) m.get("rows")).intValue();
                    Object itemsObj = m.get("items");
                    if (itemsObj instanceof Map) {
                        Map<String, Object> items = (Map<String, Object>) itemsObj;
                        for (Map.Entry<String, Object> it : items.entrySet()) {
                            String slot = it.getKey();
                            Object iv = it.getValue();
                            if (!(iv instanceof Map)) continue;
                            Map<String, Object> im = (Map<String, Object>) iv;
                            ItemDef id = new ItemDef();
                            id.material = asString(im.get("material"));
                            id.name = asString(im.get("name"));
                            Object lore = im.get("lore");
                            if (lore instanceof List) id.lore = (List<String>) lore;
                            id.action = asString(im.get("action"));
                            md.items.put(slot, id);
                        }
                    }
                    out.menus.put(key, md);
                }
            }
            return out;
        }
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
