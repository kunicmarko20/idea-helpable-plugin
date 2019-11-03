public static function with(
    {%- for property, type in properties -%}
        {{ type }} ${{ property }} {%- if (loop.last == false) -%}, {%- endif -%}
    {%- endfor -%}
): self
{
    $instance = new self();
    {%- for property, _  in properties -%}
        $instance->{{ property }} = ${{ property }};
    {%- endfor %}

    return $instance;
}
