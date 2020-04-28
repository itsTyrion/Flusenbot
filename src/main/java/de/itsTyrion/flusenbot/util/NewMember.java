package de.itsTyrion.flusenbot.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author itsTyrion
 * @since 27.04.2019
 */
@RequiredArgsConstructor(staticName = "of")
public final class NewMember {
    @Getter private final Integer id;
    @Getter private final String expectedInput;
    @Getter @Setter
    private boolean firstTry = true;
}