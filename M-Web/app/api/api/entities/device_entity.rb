module Api::Entities
  class DeviceEntity < Grape::Entity
    expose :id
    expose :device_id
    expose :device_type
    expose :device_name
    expose :device_location
    expose :created_at
  end
end
