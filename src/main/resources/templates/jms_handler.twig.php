<?php

declare(strict_types=1);

namespace {{ namespace }};

use JMS\Serializer\GraphNavigatorInterface;
use JMS\Serializer\Handler\SubscribingHandlerInterface;
use JMS\Serializer\JsonDeserializationVisitor;
use JMS\Serializer\JsonSerializationVisitor;
use {{ handling_type_fqcn }};

final class {{ name }} implements SubscribingHandlerInterface
{
    public static function getSubscribingMethods(): array
    {
        return [
            [
                'type' => {{ handling_type_name }}::class,
                'direction' => GraphNavigatorInterface::DIRECTION_DESERIALIZATION,
                'format' => 'json',
                'method' => 'deserialize',
            ],
            [
                'type' => {{ handling_type_name }}::class,
                'direction' => GraphNavigatorInterface::DIRECTION_SERIALIZATION,
                'format' => 'json',
                'method' => 'serialize',
            ],
        ];
    }

    public function deserialize(JsonDeserializationVisitor $visitor, string {{ handling_type_variable_name }}): {{ handling_type_name }}
    {
        return {{ handling_type_name }}::{{ factory_method }}({{ handling_type_variable_name }});
    }

    public function serialize(JsonSerializationVisitor $visitor, {{ handling_type_name }} {{ handling_type_variable_name }}): string
    {
        return {{ handling_type_variable_name }}->{{ to_method }}();
    }
}
