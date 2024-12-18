package no.difi.meldingsutveksling.validation.group;

import jakarta.validation.groups.Default;

public interface ValidationGroups {

    interface Partner extends Default {

        interface Sender extends Partner {
        }

        interface Receiver extends Partner {
        }
    }

    interface Create extends Default {

    }

    interface Update extends Default {

    }
}