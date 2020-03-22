private const ALL = [{% for variant in variants %}
    self::{{ variant }} => true,
{%- endfor %}
];

/**
* @var string
*/
private $value;

/**
* @var array<string, {{ type }}>
*/
private static $lazyLoad = [];

private function __construct(string $value)
{
    \Assert\Assert::that(self::ALL)->keyIsset($value);

    $this->value = $value;
}

public static function fromString(string $value): self
{
    return new self($value);
}
{% for camel_cased_variant, variant in variants %}
    public static function {{ camel_cased_variant }}(): self
    {
        return self::lazyLoad(self::{{ variant }});
    }
{% endfor %}
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

public function equals({{ type }} $other): bool
{
    return $this->value === $other->value;
}