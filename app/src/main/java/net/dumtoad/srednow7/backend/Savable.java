package net.dumtoad.srednow7.backend;

import android.support.annotation.Nullable;

import java.io.Serializable;

public interface Savable {

    Serializable getContents();

    void restoreContents(@Nullable Serializable contents) throws Exception;

}
