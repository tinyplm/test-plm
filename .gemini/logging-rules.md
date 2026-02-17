# logging-rules.md

**Quarkus Java Application — Logging Rules & Standards**

---

## 1. Purpose

This document defines mandatory logging standards for Quarkus services to ensure:

* Fast production debugging
* Consistent observability
* Searchable and structured logs
* Minimal noise with maximum diagnostic value

Logging is treated as an **operational debugging interface**, not developer narration.

---

## 2. Core Principles

1. Logs must provide actionable diagnostic information.
2. Every log entry must add debugging value.
3. Prefer fewer high-quality logs over verbose logging.
4. Logging must never impact application performance.
5. Logs must be machine-readable in production.

---

## 3. Logger Declaration (MANDATORY)

Always use JBoss Logger provided by Quarkus.

```java
import org.jboss.logging.Logger;

private static final Logger LOG = Logger.getLogger(MyService.class);
```

Rules:

* Logger must be `private static final`
* Logger name must match the class
* `System.out.println` is strictly forbidden
* Do not create dynamic loggers

---

## 4. Log Level Policy

| Level | Usage                                                          |
| ----- | -------------------------------------------------------------- |
| ERROR | Operation failure, uncaught exception, system boundary failure |
| WARN  | Recoverable anomaly or unexpected state                        |
| INFO  | Important business lifecycle events                            |
| DEBUG | Developer diagnostics                                          |
| TRACE | High-frequency internal execution flow                         |

### Global Rule

```
quarkus.log.level=INFO
```

Never enable global DEBUG in production.

Use category-based debugging instead:

```
quarkus.log.category."com.company.orders".level=DEBUG
```

---

## 5. Structured Logging (REQUIRED IN PROD)

Dependency:

```
io.quarkus:quarkus-logging-json
```

Production logs should be JSON formatted.

Benefits:

* Searchable in ELK / Datadog / Loki
* Machine parsable
* Consistent log ingestion

---

## 6. Log Message Structure

Logs must follow structured key-value format.

### ✅ Correct

```
ORDER_CREATE success orderId=123 customerId=45
```

### ❌ Incorrect

```
Order created successfully!!!
```

Rules:

* Always include identifiers
* Avoid vague messages
* Prefer `key=value` pairs
* Avoid dumping full objects

---

## 7. Exception Logging Rules

Exceptions must be logged ONLY at system boundaries:

* REST resources
* Messaging consumers
* Scheduled jobs
* External integrations

Example:

```java
LOG.errorf(e, "Payment failed paymentId=%s", paymentId);
```

Do NOT:

* Log and rethrow repeatedly
* Duplicate stack traces across layers

---

## 8. Performance Guidelines

Never construct expensive log messages.

### ❌ Bad

```java
LOG.debug("User " + user.toString());
```

### ✅ Good

```java
LOG.debugf("User id=%s", user.getId());
```

Rules:

* No string concatenation
* No serialization in logs
* DEBUG/TRACE must be cheap when disabled

---

## 9. Sensitive Data Policy

Never log:

* Passwords
* Tokens
* API keys
* Personal identifiable information
* Secrets or credentials

Mask values when required.

---

## 10. Environment Configuration

### Development

```
%dev.quarkus.log.level=DEBUG
quarkus.log.console.color=true
```

### Production

```
quarkus.log.level=INFO
quarkus.log.console.json=true
quarkus.log.console.color=false
```

---

## 11. Anti-Patterns (STRICTLY FORBIDDEN)

* Logging inside getters/setters
* Logging inside tight loops
* Logging every method entry/exit
* Logging entire entities
* Logging without context
* Multiple layers logging same exception

---

## 12. Recommended Event Pattern

Standard message format:

```
ACTION result key=value key=value reason=...
```

Examples:

```
USER_LOGIN success userId=42
PAYMENT_PROCESS failed paymentId=889 reason=Timeout
ORDER_CANCELLED success orderId=123 actor=system
```

---

## 13. Category-Based Debugging Strategy

Instead of enabling DEBUG globally:

```
quarkus.log.category."com.company.payment".level=DEBUG
```

This allows targeted debugging without log explosion.

---

## 14. Operational Rule

> One meaningful log at a state transition is better than many noisy logs.

---

## 15. LLM / Code Generator Instructions

When generating logging:

* Modify only relevant areas
* Follow log level semantics strictly
* Do not introduce extra logging
* Preserve existing logging style
* Never log sensitive data
* Prefer minimal, high-value logs

---

## 16. Definition of Done (Logging)

A feature is considered complete only if:

* Failures are diagnosable from logs
* Logs are structured and searchable
* No redundant or noisy logs exist

---

**End of logging-rules.md**
