set dotenv-load := true
set ignore-comments := true
set shell := ["bash", "-cu"]

default:
    @just --list

dev:
    ./mvnw quarkus:dev

test:
    ./mvnw test

verify:
    ./mvnw verify

package:
    ./mvnw package

uber-jar:
    ./mvnw package -Dquarkus.package.jar.type=uber-jar

native:
    ./mvnw package -Dnative

native-container:
    ./mvnw package -Dnative -Dquarkus.native.container-build=true

clean:
    ./mvnw clean

format:
    ./mvnw -DskipTests -Dformat

commit:
    git add -A
    msg=$$( { printf 'Write a concise git commit message (imperative, <= 72 chars) for this diff:\n\n'; git diff --staged; } | codex ) && git commit -m "$$msg"
