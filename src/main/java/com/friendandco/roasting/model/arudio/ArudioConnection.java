package com.friendandco.roasting.model.arudio;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ArudioConnection {
    private String exception;
    private boolean hasError = false;
}
