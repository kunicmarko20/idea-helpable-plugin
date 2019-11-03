public function with{{ capitalize(property) }}({{ property_type }} ${{ property }}):self
{
    $instance = clone $this;
    $instance->{{ property }} = ${{ property }};

    return $instance;
}