package com.github.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CodeAssistVSettings",
        storages = {
                @Storage(file = "codeassistv.xml")
        }
)
public class CodeAssistVSettings implements PersistentStateComponent<CodeAssistVSettings.State> {

    private State myState = new State();

    private static CodeAssistVSettings instance;

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.myState = state;
    }

    public static CodeAssistVSettings getInstance(){
        if(instance == null){
            instance = ServiceManager.getService(CodeAssistVSettings.class);
            if(instance == null){
                instance = new CodeAssistVSettings();
            }
        }

        return instance;
    }

    public static class State{

    }
}
