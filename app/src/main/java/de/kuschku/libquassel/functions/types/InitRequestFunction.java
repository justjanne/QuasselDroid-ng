package de.kuschku.libquassel.functions.types;

public class InitRequestFunction {
    public final String className;
    public final String objectName;

    public InitRequestFunction(String className, String objectName) {
        this.className = className;
        this.objectName = objectName;
    }

    @Override
    public String toString() {
        return "InitRequestFunction{" +
                "className='" + className + '\'' +
                ", objectName='" + objectName + '\'' +
                '}';
    }
}
