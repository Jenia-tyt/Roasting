package com.friendandco.roasting.model.units;

public enum TemperatureUnits implements Units {
    CELSIUS {
        public String getDesignation() {
            return "°C";
        }
    },

    FAHRENHEIT {
        public String getDesignation() {
            return "°F";
        }
    }
}
