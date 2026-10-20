# Smart Home Management System — Low Level Design (LLD)

## 1. Scope / Assumptions

- A home contains multiple rooms; each room can contain multiple smart devices.
- Initial device types: `SmartLight`, `SmartFan`, `SmartAC`, and `SmartDoorLock`.
- Users can register devices, view device status, turn devices on/off, and update supported settings.
- Users can group devices by room and turn all switchable devices in a room off.
- Automation rules are simulated in Java; no physical IoT hardware or network connection is required.
- Example automations: turn lights on when motion is detected and turn selected lights off at a scheduled time.
- The system records device state changes and estimates energy consumption for devices that expose power usage.
- Notifications are simulated through console/log implementations initially; SMS/email integrations are out of scope.
- Data is in memory in the first version; file persistence can be added later.
- Device IDs are unique. Invalid settings and unsupported operations must be rejected clearly.

### High-level object model

```text
                         SmartHome
                             |
                           rooms
                             |
                            Room
                             |
                           devices
                             |
                         SmartDevice
                   /         |          \
             SmartLight   SmartFan      SmartAC
                                             \
                                         (other device types)

                      SmartDoorLock

SmartHomeController ---> DeviceService / AutomationService
AutomationRule -------> Trigger + Action
SmartDevice ----------> DeviceEventListener(s)
```

`SmartDevice` is the common abstraction for all devices. Device-specific capabilities should be represented through small interfaces instead of assuming every device supports every operation.

---

## 2. Core Classes (Entities)

### 2.1 `SmartHome`

Represents the home and owns its room collection.

```text
class SmartHome
------------------------
- id: String
- name: String
- rooms: Map<String, Room>
------------------------
+ addRoom(room: Room): void
+ removeRoom(roomId: String): void
+ getRoom(roomId: String): Room
+ getRooms(): Collection<Room>
+ findDevice(deviceId: String): SmartDevice
```

### 2.2 `Room`

Groups devices by location. A room contains devices but does not implement device-specific behavior.

```text
class Room
------------------------
- id: String
- name: String
- devices: Map<String, SmartDevice>
------------------------
+ addDevice(device: SmartDevice): void
+ removeDevice(deviceId: String): void
+ getDevice(deviceId: String): SmartDevice
+ getDevices(): Collection<SmartDevice>
+ getDevicesByType(type: Class): List<SmartDevice>
```

### 2.3 `SmartDevice` (abstract base class)

Stores shared device identity and state. Keep fields private and expose controlled methods.

```text
abstract class SmartDevice
------------------------
- id: String
- name: String
- status: DeviceStatus
- connectivity: ConnectivityStatus
- ratedPowerWatts: double
- createdAt: LocalDateTime
------------------------
+ getId(): String
+ getName(): String
+ getStatus(): DeviceStatus
+ getConnectivity(): ConnectivityStatus
+ turnOn(): void
+ turnOff(): void
+ getEnergyUsageKWh(hours: double): double
+ getDetails(): String
# ensureOnline(): void
# validateCanTurnOn(): void
```

`DeviceStatus`: `ON`, `OFF`.

`ConnectivityStatus`: `ONLINE`, `OFFLINE`.

If offline devices cannot be controlled, `turnOn()` and `turnOff()` should fail with a clear exception. The simulation can start every device as online.

### 2.4 `SmartLight extends SmartDevice`

```text
class SmartLight extends SmartDevice
------------------------
- brightness: int              // 0–100
- colorMode: LightColorMode     // e.g. WARM, COOL, RGB
------------------------
+ setBrightness(value: int): void
+ getBrightness(): int
+ setColorMode(mode: LightColorMode): void
+ getColorMode(): LightColorMode
+ getDetails(): String
```

Brightness must be validated within 0–100. Whether brightness 0 automatically turns the light off is a product decision; keep it consistent throughout the implementation.

### 2.5 `SmartFan extends SmartDevice`

```text
class SmartFan extends SmartDevice
------------------------
- speedLevel: int              // 0–5
- oscillating: boolean
------------------------
+ setSpeedLevel(level: int): void
+ getSpeedLevel(): int
+ setOscillating(enabled: boolean): void
+ getDetails(): String
```

Speed must be in the supported range. Decide whether level 0 means OFF or whether power state and speed remain separate; the recommended initial rule is that turning the fan off sets its speed to 0.

### 2.6 `SmartAC extends SmartDevice`

```text
class SmartAC extends SmartDevice
------------------------
- targetTemperatureCelsius: double
- mode: ACMode                // COOL, HEAT, FAN, AUTO
- fanSpeed: FanSpeed          // LOW, MEDIUM, HIGH, AUTO
------------------------
+ setTargetTemperature(value: double): void
+ setMode(mode: ACMode): void
+ setFanSpeed(speed: FanSpeed): void
+ getDetails(): String
```

Use a defined temperature range, for example 16–30°C, and reject values outside it. Treat this as a simulation, not a real thermostat.

### 2.7 `SmartDoorLock extends SmartDevice`

A lock is not naturally a switchable device. Model its lock state separately instead of pretending `turnOn()` means “lock”.

```text
class SmartDoorLock extends SmartDevice
------------------------
- lockState: LockState        // LOCKED, UNLOCKED
------------------------
+ lock(): void
+ unlock(): void
+ getLockState(): LockState
+ getDetails(): String
```

**Design note:** A cleaner refinement is to avoid forcing door locks into power-oriented operations. Keep `SmartDevice` for identity/connectivity/common metadata and add capability interfaces such as `PowerControllable`, `BrightnessControllable`, `TemperatureControllable`, and `LockControllable`. If you use this refinement, `SmartLight`, `SmartFan`, and `SmartAC` implement the appropriate capabilities, while `SmartDoorLock` implements `LockControllable`. The controller then checks capabilities through interfaces rather than unsafe casts.

---

## 3. Capability Interfaces (Interface Segregation)

Use interfaces for operations that only some devices support.

### 3.1 `PowerControllable`

```text
interface PowerControllable
------------------------
+ turnOn(): void
+ turnOff(): void
+ getStatus(): DeviceStatus
```

### 3.2 `BrightnessControllable`

```text
interface BrightnessControllable
------------------------
+ setBrightness(value: int): void
+ getBrightness(): int
```

### 3.3 `TemperatureControllable`

```text
interface TemperatureControllable
------------------------
+ setTargetTemperature(value: double): void
+ getTargetTemperature(): double
```

### 3.4 `LockControllable`

```text
interface LockControllable
------------------------
+ lock(): void
+ unlock(): void
+ getLockState(): LockState
```

`SmartFan` may implement `PowerControllable`; `SmartLight` implements `PowerControllable` and `BrightnessControllable`; `SmartAC` implements `PowerControllable` and `TemperatureControllable`; `SmartDoorLock` implements `LockControllable`.

Avoid putting methods into one giant interface if several device types cannot meaningfully implement them.

---

## 4. Services and Controller

### 4.1 `SmartHomeController`

The application-facing entry point. It coordinates requests; it should not contain every validation rule or every device-specific operation.

```text
class SmartHomeController
------------------------
- home: SmartHome
- deviceService: DeviceService
- roomService: RoomService
- automationService: AutomationService
------------------------
+ registerDevice(roomId: String, device: SmartDevice): void
+ getDeviceStatus(deviceId: String): String
+ turnDeviceOn(deviceId: String): void
+ turnDeviceOff(deviceId: String): void
+ turnRoomDevicesOff(roomId: String): void
+ setBrightness(deviceId: String, value: int): void
+ setTemperature(deviceId: String, value: double): void
+ lockDoor(deviceId: String): void
+ unlockDoor(deviceId: String): void
```

### 4.2 `DeviceService`

Contains device lookup and device-level use cases.

```text
class DeviceService
------------------------
- deviceRepository: DeviceRepository
- eventPublisher: DeviceEventPublisher
------------------------
+ registerDevice(room: Room, device: SmartDevice): void
+ findById(deviceId: String): SmartDevice
+ turnOn(deviceId: String): void
+ turnOff(deviceId: String): void
+ updateSetting(deviceId: String, setting: DeviceSetting): void
+ getStatus(deviceId: String): DeviceStatus
```

If the project is kept fully in memory, `DeviceRepository` can be a simple map-backed class. It is optional in the first milestone, but helps demonstrate dependency inversion and testing.

### 4.3 `RoomService`

```text
class RoomService
------------------------
- home: SmartHome
------------------------
+ createRoom(room: Room): void
+ addDevice(roomId: String, device: SmartDevice): void
+ removeDevice(roomId: String, deviceId: String): void
+ turnOffAllPowerControllableDevices(roomId: String): void
+ getRoomDevices(roomId: String): List<SmartDevice>
```

“Turn all devices off” should be implemented as “turn off all power-controllable devices”. It should not try to turn a door lock off; lock/unlock is a different capability.

---

## 5. Automation Design

Automation rules combine a trigger with an action. This keeps rules extensible and avoids embedding every scenario in a long `if/else` chain.

### 5.1 `AutomationRule`

```text
class AutomationRule
------------------------
- id: String
- name: String
- enabled: boolean
- trigger: AutomationTrigger
- action: AutomationAction
------------------------
+ enable(): void
+ disable(): void
+ evaluate(context: AutomationContext): void
```

### 5.2 Trigger abstraction and implementations

```text
interface AutomationTrigger
------------------------
+ isTriggered(context: AutomationContext): boolean

class MotionDetectedTrigger implements AutomationTrigger
+ isTriggered(context: AutomationContext): boolean

class TimeTrigger implements AutomationTrigger
- targetTime: LocalTime
+ isTriggered(context: AutomationContext): boolean
```

For a beginner-friendly simulation, pass the current time and motion events into `AutomationContext`; do not create real background schedulers in the first version.

### 5.3 Action abstraction and implementations

```text
interface AutomationAction
------------------------
+ execute(context: AutomationContext): void

class TurnOnDeviceAction implements AutomationAction
- deviceId: String
+ execute(context: AutomationContext): void

class TurnOffDeviceAction implements AutomationAction
- deviceId: String
+ execute(context: AutomationContext): void

class TurnOffRoomLightsAction implements AutomationAction
- roomId: String
+ execute(context: AutomationContext): void
```

### 5.4 `AutomationService`

```text
class AutomationService
------------------------
- rules: List<AutomationRule>
- context: AutomationContext
------------------------
+ addRule(rule: AutomationRule): void
+ removeRule(ruleId: String): void
+ enableRule(ruleId: String): void
+ disableRule(ruleId: String): void
+ evaluateRules(): void
+ handleMotionDetected(roomId: String): void
```

Example rule: when motion is detected in the hallway, turn on the hallway light. Another rule can turn selected lights off when the simulated time reaches 22:00.

---

## 6. Events, Notifications, and Energy Monitoring

### 6.1 Observer pattern — device events

```text
class DeviceEvent
------------------------
- deviceId: String
- eventType: DeviceEventType
- message: String
- timestamp: LocalDateTime

interface DeviceEventListener
------------------------
+ onDeviceEvent(event: DeviceEvent): void

class ConsoleNotificationListener implements DeviceEventListener
+ onDeviceEvent(event: DeviceEvent): void

class DeviceEventPublisher
------------------------
- listeners: List<DeviceEventListener>
------------------------
+ subscribe(listener: DeviceEventListener): void
+ unsubscribe(listener: DeviceEventListener): void
+ publish(event: DeviceEvent): void
```

Events can include `DEVICE_TURNED_ON`, `DEVICE_TURNED_OFF`, `SETTING_CHANGED`, `DEVICE_OFFLINE`, `DOOR_LOCKED`, and `DOOR_UNLOCKED`.

Publish an event after a successful state change. Avoid reporting success when validation fails.

### 6.2 Energy monitoring

```text
class EnergyMonitor
------------------------
+ estimateKWh(device: SmartDevice, hours: double): double
+ estimateRoomKWh(room: Room, hours: double): double
+ estimateHomeKWh(home: SmartHome, hours: double): double
```

Basic estimate:

`energy (kWh) = rated power (watts) × operating hours ÷ 1000`

This is a simplified estimate. For the initial version, count only devices that are ON and have a known rated power. It does not represent measured real-world energy consumption.

---

## 7. Supporting Design Patterns

### 7.1 `DeviceFactory` — Factory Pattern

Centralizes device creation.

```text
class DeviceFactory
------------------------
+ createDevice(type: DeviceType, id: String, name: String): SmartDevice
```

The factory may use a `switch` in one place. If device construction becomes more complex, it can be refined to a registry of creators.

### 7.2 `AutomationAction` — Strategy-style polymorphism

Different actions implement the same interface, so the automation rule can execute an action without knowing its concrete class.

### 7.3 `DeviceEventPublisher` — Observer Pattern

Listeners receive device events without the device or service needing to know whether the listener writes to a console, file, or later an external notification provider.

### 7.4 Dependency Injection

Pass services and repositories through constructors rather than creating concrete dependencies inside the controller.

```text
SmartHomeController(
    home,
    deviceService,
    roomService,
    automationService
)
```

This makes the system easier to test and allows implementations to be replaced.

---

## 8. Enums and Exceptions

### Enums

```text
DeviceStatus: ON, OFF
ConnectivityStatus: ONLINE, OFFLINE
DeviceType: LIGHT, FAN, AC, DOOR_LOCK
LightColorMode: WARM, COOL, RGB
ACMode: COOL, HEAT, FAN, AUTO
FanSpeed: LOW, MEDIUM, HIGH, AUTO
LockState: LOCKED, UNLOCKED
DeviceEventType: DEVICE_TURNED_ON, DEVICE_TURNED_OFF,
                 SETTING_CHANGED, DEVICE_OFFLINE,
                 DOOR_LOCKED, DOOR_UNLOCKED
```

### Custom exceptions

```text
class DeviceNotFoundException extends RuntimeException
class RoomNotFoundException extends RuntimeException
class InvalidDeviceSettingException extends RuntimeException
class DeviceOfflineException extends RuntimeException
class UnsupportedDeviceOperationException extends RuntimeException
class DuplicateDeviceException extends RuntimeException
```

Use exceptions for invalid IDs, duplicate registration, offline operations, invalid brightness/temperature/speed, and unsupported capabilities. Validate at the service/domain boundary and keep the error messages understandable.

---

## 9. Relationships Summary

| Relationship | Type | Multiplicity |
|---|---|---|
| `SmartHome` → `Room` | Composition (in this model) | 1 → 1..* |
| `Room` → `SmartDevice` | Aggregation or composition, depending on lifecycle | 1 → 0..* |
| `SmartLight` → `SmartDevice` | Inheritance | 1 → 1 |
| `SmartFan` → `SmartDevice` | Inheritance | 1 → 1 |
| `SmartAC` → `SmartDevice` | Inheritance | 1 → 1 |
| `SmartDoorLock` → `SmartDevice` | Inheritance | 1 → 1 |
| `SmartHomeController` → services | Association / dependency injection | 1 → 1 each |
| `AutomationRule` → `AutomationTrigger` | Composition | 1 → 1 |
| `AutomationRule` → `AutomationAction` | Composition | 1 → 1 |
| `DeviceEventPublisher` → listeners | Association | 1 → 0..* |

**Lifecycle note:** Removing a room should not necessarily destroy its physical devices. For a realistic model, `Room` groups devices while a repository/home registry manages device identity. Choose and document one lifecycle policy rather than mixing composition and aggregation casually.

---

## 10. Mapping to OOP Pillars and SOLID

| Concept | Where it is applied |
|---|---|
| Encapsulation | Device state and settings are private and changed through validated methods. |
| Abstraction | `SmartDevice`, `AutomationTrigger`, `AutomationAction`, and capability interfaces expose contracts. |
| Inheritance | Concrete device classes reuse common `SmartDevice` identity and behavior. |
| Polymorphism | Different trigger/action implementations execute through shared interfaces; device details can be overridden. |
| SRP | Device state, room grouping, automation, energy estimates, and notifications are separate responsibilities. |
| OCP | Add a new automation action or trigger by implementing its interface instead of editing the rule engine. |
| LSP | Device subclasses must preserve the promises of their base class; capability interfaces prevent invalid assumptions about locks. |
| ISP | Small interfaces separate power, brightness, temperature, and lock operations. |
| DIP | The controller depends on service abstractions/injected collaborators, not manually constructed concrete dependencies. |

---

## 11. Key Flows

### 11.1 Turn on a light

1. Client calls `SmartHomeController.turnDeviceOn(deviceId)`.
2. `DeviceService` looks up the device.
3. Service verifies the device exists and is online.
4. Service verifies it supports `PowerControllable`.
5. The device validates and changes its state to `ON`.
6. An event is published after the successful state change.
7. The caller receives success; any validation failure is surfaced as a clear exception.

### 11.2 Turn off all power-controllable devices in a room

1. Client calls `turnRoomDevicesOff(roomId)`.
2. `RoomService` finds the room or raises `RoomNotFoundException`.
3. It iterates through the room's devices.
4. For each device implementing `PowerControllable`, it calls `turnOff()`.
5. Door locks are skipped because locking is not the same as powering off.
6. The service records or reports any failures; one offline device should not silently make the whole operation appear successful.

### 11.3 Motion-triggered lighting

1. A simulated motion event arrives with a room ID.
2. `AutomationService` builds or updates the automation context.
3. Enabled rules evaluate their triggers.
4. A matching `MotionDetectedTrigger` returns `true`.
5. Its `TurnOnDeviceAction` executes through the device service.
6. The light changes state and publishes a device event.

---

## 12. Suggested Package Structure

```text
com.smarthome.model
    SmartHome, Room, SmartDevice, SmartLight, SmartFan, SmartAC, SmartDoorLock

com.smarthome.capability
    PowerControllable, BrightnessControllable,
    TemperatureControllable, LockControllable

com.smarthome.controller
    SmartHomeController

com.smarthome.service
    DeviceService, RoomService, AutomationService, EnergyMonitor

com.smarthome.automation
    AutomationRule, AutomationContext, AutomationTrigger,
    MotionDetectedTrigger, TimeTrigger, AutomationAction,
    TurnOnDeviceAction, TurnOffDeviceAction, TurnOffRoomLightsAction

com.smarthome.event
    DeviceEvent, DeviceEventPublisher, DeviceEventListener,
    ConsoleNotificationListener

com.smarthome.factory
    DeviceFactory

com.smarthome.repository
    DeviceRepository, InMemoryDeviceRepository

com.smarthome.enums
    DeviceStatus, ConnectivityStatus, DeviceType, LightColorMode,
    ACMode, FanSpeed, LockState, DeviceEventType

com.smarthome.exception
    DeviceNotFoundException, RoomNotFoundException,
    InvalidDeviceSettingException, DeviceOfflineException,
    UnsupportedDeviceOperationException, DuplicateDeviceException
```

---

## 13. Suggested Build Order

1. **Milestone 1 — Basic OOP:** enums, `SmartDevice`, `SmartLight`; constructors, private fields, getters, ON/OFF, brightness validation.
2. **Milestone 2 — Device polymorphism:** add `SmartFan`, `SmartAC`, and `SmartDoorLock`; use capability interfaces so unsupported operations are not forced on every device.
3. **Milestone 3 — Composition and collections:** implement `SmartHome`, `Room`, device registration/removal, lookup by ID, and duplicate-ID checks.
4. **Milestone 4 — Services and SOLID:** introduce controller and services; use constructor injection and separate responsibilities.
5. **Milestone 5 — Events and patterns:** add device event publishing, console notifications, and `DeviceFactory`.
6. **Milestone 6 — Automation:** implement trigger/action interfaces and simulate motion/time-based rules.
7. **Milestone 7 — Energy and exceptions:** calculate estimated kWh, validate settings, and test failure cases.
8. **Milestone 8 — Persistence and testing:** save/load state from files, add unit tests, and optionally build a GUI.

## 14. Keep the First Version Manageable

For a Java OOP minor project, first complete the console-based version with rooms, devices, state changes, validation, and tests. Add automation, observers, energy estimates, and persistence only after the core model works.

Do not add physical IoT hardware, mobile apps, databases, cloud services, or real SMS/email integrations unless they are explicitly required. The simulated version is enough to demonstrate OOP and design principles cleanly.
