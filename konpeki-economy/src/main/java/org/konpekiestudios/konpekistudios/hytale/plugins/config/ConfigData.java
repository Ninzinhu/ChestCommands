//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.config;

public class ConfigData {
    public Storage storage;
    public Chat chat;
    public Balance balance;
    public Messages messages;

    public static class Storage {
        public String type;
        public MySQL mysql;

        public static class MySQL {
            public String host;
            public int port;
            public String database;
            public String username;
            public String password;
        }
    }

    public static class Chat {
        public String currency;
        public int decimal_places;
    }

    public static class Balance {
        public float starting;
        public float maximum;
        public float minimum;
    }

    public static class Messages {
        public String money_sent;
        public String money_received;
        public String insufficient_funds;
        public String balance_check;
        public String unknown_player;
        public String cannot_pay_self;
        public String max_balance_reached;
        public String min_balance_reached;
        public String admin_add_success;
        public String admin_remove_success;
        public String admin_set_success;
        public String error_occurred;
    }
}
