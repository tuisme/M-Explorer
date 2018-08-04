module Api::Entities
  class UserEntity < Grape::Entity
    expose :token
    expose :email
    expose :first_name
    expose :last_name
    expose :avatar_url
    expose :used
    expose :allocated
    expose :is_vip
  end
end
