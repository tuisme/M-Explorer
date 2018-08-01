module Api::Entities
  class CloudEntity < Grape::Entity
    expose :id
    expose :cloud_name, as: :name
    expose :cloud_root, as: :root
    expose :cloud_type, as: :type
    expose :access_token, as: :token
    expose :used
    expose :allocated
  end
end
