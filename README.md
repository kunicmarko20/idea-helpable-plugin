Helpable
========

A PHPStorm plugin that handles all the boilerplate code at [Lendable](https://github.com/Lendable).

## What does this plugin do

This plugin generates code snippets and creates new files.

## Actions

* [With*](#with)
* [To*](#to)
* [Equals](#equals)
* [Factory Method](#factory-method)
* [Enum](#enum)
* [JMS Serializer XML Configuration](#jms-serialize-xml-configuration)
* [JMS Serializer Handler](#jms-serializer-handler)

### With*

If there is only 1 property in your class, `With*` Generator will that property and generate a `wither` method
that looks like:

```php
public function withName(string $name):self
{
    $instance = clone $this;
    $instance->name = $name;

    return $instance;
}
```

If there is more than 1 field, it will let you choose one or multiple fields for which you want to generate a wither.

### To*

`To*` will generate a representation method for your class based on selected property that looks like:

```php
public function toString(): string
{
    return $this->name;
}
```

If there is more than 1 property it will let you choose which property you want, if there is only 1 it will take that one.

### Equals

Equals takes all properties in the class and generates an equals method that looks like:
 
```php
public function equals(Name $other): bool
{
    return $this->name === $other->name;
}
```

### Factory Method

Factory Method takes all properties in the class and creates generic factory method that looks like:

```php
public static function with(
    FirstName $firstName,
    LastName $lastName
): self {
    $instance = new self();

    $instance->firstName = $firstName;
    $instance->lastName = $lastName;

    return $instance;
}
```
### Enum

Enum method requires you to have a class with only constants that represent enum variants and adds all needed
code to it, it converts:

```php
final class Light
{
    private const GREEN = 'green';

    private const YELLOW = 'yellow';

    private const RED = 'red';
}
```

into:

```php
final class Light
{
    private const GREEN = 'green';

    private const YELLOW = 'yellow';

    private const RED = 'red';

    private const ALL = [
        self::GREEN,
        self::YELLOW,
        self::RED,
    ];
    
    /**
    * @var string
    */
    private $value;
    
    /**
    * @var array<string, Light>
    */
    private static $lazyLoad = [];
    
    private function __construct(string $value)
    {
        \Assert\Assert::that($value)->inArray(self::ALL);
    
        $this->value = $value;
    }
    
    public static function fromString(string $value): self
    {
        return new self($value);
    }

    public static function green(): self
    {
        return self::lazyLoad(self::GREEN);
    }

    public static function yellow(): self
    {
        return self::lazyLoad(self::YELLOW);
    }

    public static function red(): self
    {
        return self::lazyLoad(self::RED);
    }

    private static function lazyLoad(string $value): self
    {
        if (isset(self::$lazyLoad[$value])) {
            return self::$lazyLoad[$value];
        }
    
        return self::$lazyLoad[$value] = new self($value);
    }
    
    public function toString(): string
    {
        return $this->value;
    }
    
    public function equals(Light $other): bool
    {
        return $this->value === $other->value;
    }
}
```

### JMS Serialize XML Configuration

This action creates a new XML JMS Configuration file based on selected class. You get a dialog
with a textbox that autocompletes php classes in your project, after you select a class you get
a file like:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<serializer>
    <class name="MyProject\Name" exclusion-policy="ALL">
        <property name="firstName" type="MyProject\FirstName" expose="true" />
        <property name="lastName" type="MyProject\LastName" expose="true" />
    </class>
</serializer>
```

### JMS Serializer Handler

This action creates a new JMS Handler file based on selected class. You get a dialog
with a textbox that autocompletes php classes in your project, after you select a class you get
a file like:

```php
<?php

declare(strict_types=1);

namespace MyProject\JMSSerializer;

use JMS\Serializer\GraphNavigatorInterface;
use JMS\Serializer\Handler\SubscribingHandlerInterface;
use JMS\Serializer\JsonDeserializationVisitor;
use JMS\Serializer\JsonSerializationVisitor;
use MyProject\FirstName;

final class FirstNameHandler implements SubscribingHandlerInterface
{
    public static function getSubscribingMethods(): array
    {
        return [
            [
                'type' => FirstName::class,
                'direction' => GraphNavigatorInterface::DIRECTION_DESERIALIZATION,
                'format' => 'json',
                'method' => 'deserialize',
            ],
            [
                'type' => FirstName::class,
                'direction' => GraphNavigatorInterface::DIRECTION_SERIALIZATION,
                'format' => 'json',
                'method' => 'serialize',
            ],
        ];
    }

    public function deserialize(JsonDeserializationVisitor $visitor, string $firstName, array $type): FirstName
    {
        return FirstName::fromString($firstName);
    }

    public function serialize(JsonSerializationVisitor $visitor, FirstName $firstName, array $type): string
    {
        return $firstName->toString();
    }
}
```
