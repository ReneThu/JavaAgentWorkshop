---
theme: default
title: "How does a Java Agent work?"
info: |
  ## How does a Java Agent work?
  Building a Java agent from scratch — a hands-on workshop.
drawings:
  persist: false
transition: slide-up
mdc: true
layout: center
---

<h1 class="headline">How does a Java Agent work?</h1><br />
<h2 class="headline-smol">Building a Java agent from scratch</h2>

<div class="addonestuff">Marco Sussitz — Senior Software Developer at Dynatrace</div>
<br /><br />
<div class="centerLogo">
    <img src="./pictures/Dynatrace_Logo.png" alt=""/>
</div>

---
layout: center
---

# How do those things work?

<v-clicks>

- Debuggers
- Code hot swapping
- Observability e.g. the Otel agent

</v-clicks>

---
layout: center
---

<v-clicks>

# How do they work?

## Java Virtual Machine Tool Interface (JVM TI)

- Access to low level functions

</v-clicks>

---
layout: center
---

<v-click>
  <div class="image">
      <img src="./pictures/applePie.png" alt=""/>
  </div>
</v-click>

<!--
There is a quote from Carl Sagan.
If you wish to make an apple pie from scratch, you must first invent the universe.

If we really want to understand how code hotswapping works we first need to understand
how classes are loaded in the JVM
-->

---
layout: center
---

<v-click>
  <h1>What happens during class loading?</h1>
</v-click>

<div>
  <ul>
      <li v-click>
        <span v-mark.underscore.orange=6>
          Loading
        </span>
      </li>
    <li v-click>Verify</li>
    <li v-click>Prepare</li>
    <li v-click>(Optionally) Resolve</li>
  </ul>
</div>

<!--
chapter 12 of the java language specification as well as chapter 5 of the java virtual machine specification talk about that.
So there are 5 steps that are taken.

The first one loading. 
So if a class is requested that is not loaded classloader will be used to look for a binary representation of the class.

Verification:
This means that the class is checked that it is well-formatted. So with a proper symbol table and so on.

Preparation:
static storage and any data structures

Resolution(optional):

checking symbolic references from the class to other
classes and interfaces.

The Loading step is the one we are interested in.
-->

---
layout: center
---

<v-click>
  <h1>Observe when classes are loaded</h1>
</v-click>

<v-clicks>
<div>
```java{all}
void JNICALL
ClassFileLoadHook(jvmtiEnv *jvmti_env,
        JNIEnv* jni_env,
        jclass class_being_redefined,
        jobject loader,
        const char* name,
        jobject protection_domain,
        jint class_data_len,
        const unsigned char* class_data,
        jint* new_class_data_len,
        unsigned char** new_class_data)
```
</div>

</v-clicks>

---
layout: center
---

<v-clicks>
<h1>Is there a better way?</h1>
</v-clicks>

<div>
  <ul>
    <li v-click>Java Agents</li>
    <li v-click>java.lang.instrument</li>
    <li v-click>java -javaagent:agent.jar -jar helloWorld.jar</li>
  </ul>
</div>

---
layout: center
---

<v-clicks>

````md magic-move{lines: true}
```java{all|2}
public class SampleAgent {
    public static void premain(String arguments, Instrumentation instrumentationObject) {
    } 
}
```

```java{all|3}
public class SampleAgent {
    public static void premain(String arguments, Instrumentation instrumentationObject) {
        instrumentationObject.addTransformer(new OptimusPrime(), false);
    }
}
```
````
</v-clicks>

---
layout: center
---

<v-clicks at="1">
<div>
````md magic-move{lines: true}
```java{all|all|3-7|3|4|5|6|7|8|all}
public class OptimusPrime implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classFileBuffer) throws IllegalClassFormatException {
        return transformClassFile(classFileBuffer);
    }
}
```
```java{all}
public class OptimusPrime implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classFileBuffer) throws IllegalClassFormatException {
        return transformClassFile(classFileBuffer);
    }

    public byte[] transformClassFile(byte[] classfileBuffer) {
        //TODO
        return null;
    }
}
```
````
</div>
</v-clicks>

---
layout: center
---

# Agent JAR Manifest

<v-clicks>

```properties
Premain-Class: com.example.agent.MyAgent
Can-Retransform-Classes: true
Can-Redefine-Classes: true
```

- Tells the JVM which class contains `premain`
- Without this → agent won't load
- Gradle generates this for us automatically

</v-clicks>


---
layout: center
---

# Requirements

<v-clicks>

- **JDK 17+** installed
- **Gradle 8+** (wrapper included)
- **IDE** — IntelliJ IDEA recommended
- **Git** 
- Basic Java knowledge

</v-clicks>


---
layout: center
---


<v-clicks>

# What are we going to do?

- Build a basic Java agent
- Add runtime code manipulation
- Load classes at runtime
- Add basic "Observability" to our spring boot App

</v-clicks>

---
layout: center
---

# Running with `-javaagent`

<v-clicks>

```bash
java -javaagent:agent.jar -jar sample-app.jar
```

```
[Agent] Hello from premain! The agent is loaded.

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 ...
```

- Agent loads first → premain runs → then Spring Boot starts
- https://github.com/ReneThu/JavaAgentWorkshop
- Check out this tag: start

</v-clicks>

---
layout: center
---

# Find our target class

<v-clicks>

```java
public byte[] transform(ClassLoader loader, String className,
                        Class<?> classBeingRedefined,
                        ProtectionDomain protectionDomain,
                        byte[] classfileBuffer) {
    if (!TARGET.equals(className)) {
        return null;
    }
}
```
</v-clicks>

---
layout: center
---

# Quick Detour: What is a class file?

<v-clicks>

- A Java class file is the compiled bytecode output of a Java source file, usually ending in .class
- It contains instructions that the Java Virtual Machine (JVM) can execute
- We can use tools like javap or ASM Bytecode Viewer to inspect them

</v-clicks>

---
layout: center
---

```java {all} twoslash
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
```

---
layout: center
---

```yaml {all|2-3|4|7} twoslash
public class com.example.app.HelloController
    minor version: 0
    major version: 61
    flags: (0x0021) ACC_PUBLIC, ACC_SUPER
    this_class: #9                          // com/example/app/HelloController
    super_class: #2                         // java/lang/Object
    interfaces: 0, fields: 0, methods: 2, attributes: 2
```

---
layout: center
---

```yaml {all|22} twoslash
Constant pool:
    #1 = Methodref          #2.#3          // java/lang/Object."<init>":()V
    #2 = Class              #4             // java/lang/Object
    #3 = NameAndType        #5:#6          // "<init>":()V
    #4 = Utf8               java/lang/Object
    #5 = Utf8               <init>
    #6 = Utf8               ()V
    #7 = String             #8             // Hello, World!
    #8 = Utf8               Hello, World!
    #9 = Class              #10            // com/example/app/HelloController
    #10 = Utf8               com/example/app/HelloController
    #11 = Utf8               Code
    #12 = Utf8               LineNumberTable
    #13 = Utf8               LocalVariableTable
    #14 = Utf8               this
    #15 = Utf8               Lcom/example/app/HelloController;
    #16 = Utf8               hello
    #17 = Utf8               ()Ljava/lang/String;
    #18 = Utf8               RuntimeVisibleAnnotations
    #19 = Utf8               Lorg/springframework/web/bind/annotation/GetMapping;
    #20 = Utf8               value
    #21 = Utf8               /hello
    #22 = Utf8               SourceFile
    #23 = Utf8               HelloController.java
    #24 = Utf8               Lorg/springframework/web/bind/annotation/RestController;

```

---
layout: center
---

```yaml {all|1|2|6-8|9-10|11-13} twoslash
  public com.example.app.HelloController();
  descriptor: ()V
  flags: (0x0001) ACC_PUBLIC
  Code:
    stack=1, locals=1, args_size=1
    0: aload_0
    1: invokespecial #1                  // Method java/lang/Object."<init>":()V
    4: return
    LineNumberTable:
      line 7: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
      0       5     0  this   Lcom/example/app/HelloController;
```

---
layout: center
---

```yaml {all|1|2|6-7|13-16} twoslash
  public java.lang.String hello();
  descriptor: ()Ljava/lang/String;
  flags: (0x0001) ACC_PUBLIC
  Code:
    stack=1, locals=1, args_size=1
    0: ldc           #7                  // String Hello, World!
    2: areturn
    LineNumberTable:
      line 11: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
      0       3     0  this   Lcom/example/app/HelloController;
  RuntimeVisibleAnnotations:
    0: #19(#20=[s#21])
      org.springframework.web.bind.annotation.GetMapping(
      value=["/hello"]

```

---
layout: center
---

- Java Just Got Easier: How the Class File API Simplifies Updating your JVM

  <div class="image">
      <img src="./pictures/byteBuddey.png" alt=""/>
  </div>

---
layout: center
---

# How do we modify bytecode?

<v-clicks>

- With ASM
- ASM is an all purpose Java bytecode manipulation and analysis framework.
- It makes heavy use of the visitor pattern

```text
ClassReader  ──▶  ClassVisitor  ──▶  ClassWriter
                       │
                       ▼
                 MethodVisitor (per method)
```
</v-clicks>

---
layout: center
---

# How to use it

<v-clicks>

```java
@Override
public byte[] transform(ClassLoader loader, String className,
                        Class<?> classBeingRedefined,
                        ProtectionDomain protectionDomain,
                        byte[] classfileBuffer) {
    ...
    ClassReader reader = new ClassReader(classfileBuffer);
    ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
    reader.accept(new RequestClassVisitor(writer), 0);
    return writer.toByteArray();
}
```



</v-clicks>

---
layout: center
---

```java
@Override
public MethodVisitor visitMethod(int access, String name, String descriptor,
                                 String signature, String[] exceptions) {
    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
    if (/**Some Filter**/) {
        return new CustomMethodVisitor(mv);
    }
    return mv;
}
```
---
layout: center
---

# Adding method calls

<v-clicks>

- Find the HelloController
- Find the hello method.
- Add a call to System.out.println(..)

</v-clicks>

---
layout: center
---

# This is not fun

<v-clicks>

- We want to write as little bytecode as possible
- Ideally we only call a helper method and are then done with bytecode.


</v-clicks>

---
layout: center
---

# But just calling a static helper method might not work.

<v-clicks>

- Classloader visibility

</v-clicks>

---
layout: center
---

# Quick Detour: What is a classLoader?

<v-clicks>

- Load .class files into the JVM
- Control visibility by determining which classes a given loader can see
- Delegate loading to parent class loaders for core Java classes

</v-clicks>

---
layout: center
---

# How do we load our helper class in the correct classLoader?

---
layout: center
---


<v-clicks>

# Which Method Should We Instrument?

</v-clicks>

<v-clicks>

- We want to intercept **all** web requests
- As little work as possible
- Add an entry and exit call

</v-clicks>

---
layout: center
---

# Pass information across method calls

- It would be nice if we could persist some information across multiple method calls


---
layout: center
---

- Please rate this session

  <div class="image">
      <img src="./pictures/rating.png" alt=""/>
  </div>

---
layout: center
---

<h1 class="headline">Questions?</h1>

<br />
<div class="centerLogo">
    <img src="./pictures/Dynatrace_Logo.png" alt=""/>
</div>
