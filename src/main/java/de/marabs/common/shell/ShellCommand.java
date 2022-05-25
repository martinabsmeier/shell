/*
 * Copyright 2022 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.marabs.common.shell;

import de.marabs.common.shell.exception.ShellException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Command table entry
 *
 * @author Martin Absmeier
 */
public class ShellCommand {

    @Getter
    private final String prefix;
    @Getter
    private final String name;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private String abbreviation;
    @Getter
    private final Method method;
    @Getter @Setter
    private String header;
    private final Object handler;
    @Getter
    private final ShellCommandParamSpec[] paramSpecs;

    public ShellCommand(Object handler, Method method, String prefix, String name) {
        assert method != null;
        this.paramSpecs = ShellCommandParamSpec.forMethod(method);
        assert paramSpecs.length == method.getParameterTypes().length;
        this.method = method;
        this.prefix = prefix;
        this.name = name;
        this.handler = handler;

        this.description = makeCommandDescription(method, paramSpecs);
    }

    private static String makeCommandDescription(Method method, ShellCommandParamSpec[] paramSpecs) {
        StringBuilder result = new StringBuilder();
        result.append(method.getName());
        result.append('(');
        Class[] paramTypes = method.getParameterTypes();
        assert paramTypes.length == paramSpecs.length;
        boolean first = true;
        for (int i = 0; i < paramTypes.length; i++) {
            if (!first) {
                result.append(", ");
            }
            first = false;
            if (paramSpecs[i] != null) {
                result.append(paramSpecs[i].getName());
                result.append(":");
                result.append(paramTypes[i].getSimpleName());
            } else {
                result.append(paramTypes[i].getSimpleName());
            }
        }
        result.append(") : ");
        result.append(method.getReturnType().getSimpleName());
        return result.toString();
    }

    public Object invoke(Object[] parameters) throws ShellException {
        assert method != null;
        try {
            return method.invoke(handler, parameters);
        } catch (InvocationTargetException ite) {
            return ite.getCause();
        } catch (Exception ex) {
            throw new ShellException(ex);
        }
    }

    public boolean canBeDenotedBy(String commandName) {
        return commandName.equals(prefix + name) || commandName.equals(prefix + abbreviation);
    }

    public int getArity() {
        return method.getParameterTypes().length;
    }

    public boolean startsWith(String prefix) {
        return (this.prefix + abbreviation).startsWith(prefix) || (this.prefix + name).startsWith(prefix);
    }

    @Override
    public String toString() {
        return prefix + name + "\t" + (abbreviation != null ? prefix + abbreviation : "") + "\t" +
            method.getParameterTypes().length + (method.isVarArgs() ? "+" : "") + "\t" + description;
    }
}