module Api::V2
  class UserApi < Grape::API
    namespace :users do

      desc 'Provider'
      params do
        requires :provider, type: String
        requires :email, type: String
        requires :first_name, type: String
        requires :last_name, type: String
        requires :device_id, type: String
        requires :device_type, type: String
        requires :device_name, type: String
        requires :device_location, type: String
      end
      post :provider do
        return_user = OpenStruct.new
        user = User.find_by(email: params[:email])
        if user.present?
          return_user.token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: return_user.token, device_id: params[:device_id], device_type: params[:device_type],
                             device_name: params[:device_name], device_location: params[:device_location])
          return_user.email = user.email
          return_user.first_name = user.first_name
          return_user.last_name = user.last_name
          return_user.avatar_url = user.avatar_url
          return_user.used = user.used
          return_user.allocated = user.allocated
          return_user.is_vip = user.vip

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Login Successfully!'
          present :data, return_user, with: Api::Entities::UserEntity
        else
          password = SecureRandom.urlsafe_base64(nil, true)
          user = User.create(provider: params[:provider], email: params[:email], password: password, uid: params[:uid], first_name: params[:first_name],
                             last_name: params[:last_name], avatar_url: ENV['default_avatar'], used: 0, allocated: ENV['default_space'], vip: :false)
          return_user.email = user.email
          return_user.first_name = user.first_name
          return_user.last_name = user.last_name
          return_user.avatar_url = user.avatar_url
          return_user.used = user.used
          return_user.allocated = user.allocated
          return_user.is_vip = user.vip
          return_user.token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: return_user.token, device_id: params[:device_id], device_name: params[:device_name])
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Login Successfully!'
          present :data, return_user, with: Api::Entities::UserEntity
        end
      end

      desc 'Signin'
      params do
        requires :email, type: String
        requires :password, type: String
        requires :device_id, type: String
        requires :device_type, type: String
        requires :device_name, type: String
        requires :device_location, type: String
      end
      post :signin do
        return_user = OpenStruct.new
        user = User.find_by(email: params[:email])
        raise ActiveRecord::RecordNotFound.new('User not found!') unless user
        if user.valid_password?(params[:password])
          return_user.token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: return_user.token, device_id: params[:device_id], device_type: params[:device_type],
                             device_name: params[:device_name], device_location: params[:device_location])
          return_user.email = user.email
          return_user.first_name = user.first_name
          return_user.last_name = user.last_name
          return_user.avatar_url = user.avatar_url
          return_user.used = user.used
          return_user.allocated = user.allocated
          return_user.is_vip = user.vip

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Login Successfully!'
          present :data, return_user, with: Api::Entities::UserEntity
        else
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Invalid email or password!'
        end
      end

      desc 'Signup'
      params do
        requires :email, type: String
        requires :password, type: String
        requires :first_name, type: String
        requires :last_name, type: String
      end
      post :signup do
        if User.create!(email: params[:email], password: params[:password], first_name: params[:first_name],
           last_name: params[:last_name], avatar_url: ENV['default_avatar'], used: 0, allocated: ENV['default_space'], vip: 'false')
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Registration successful. You can log in now!'
        end
      end

      desc 'Forget password'
      params do
        requires :email, type: String
      end
      post :forget_password do
        if User.find_by_email(params[:email])
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Reset password email has been sent!'
        else
          present :time, Time.now.to_s
          present :status, 'error'
          present :message, "User doesn't exist!"
        end
      end

      desc 'Update profile'
      params do
        requires :first_name, type: String
        requires :last_name, type: String
        requires :avatar, type: File
      end
      put do
        if current_token
          return_user = OpenStruct.new
          client = Imgur.new(ENV['imgur_client_id'])
          title = SecureRandom.urlsafe_base64(nil, true)
          image = Imgur::LocalImage.new(params[:avatar][:tempfile].path, title: title)
          uploaded = client.upload(image)
          current_user.update(first_name: params[:first_name], last_name: params[:last_name], avatar_url: uploaded.link)

          return_user.token = current_token.value
          return_user.email = current_user.email
          return_user.first_name = current_user.first_name
          return_user.last_name = current_user.last_name
          return_user.avatar_url = current_user.avatar_url
          return_user.used = current_user.used
          return_user.allocated = current_user.allocated
          return_user.is_vip = current_user.vip

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Update Successfully!'
          present :data, return_user, with: Api::Entities::UserEntity
        else
          present :time, Time.now.to_s
          present :status, 'error'
          present :message, 'You need to be logged in to access!'
        end
      end

      desc 'Logout'
      delete do
        if current_token
          Token.destroy(current_token.id)
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'You have successfully logged out!'
        else
          present :time, Time.now.to_s
          present :status, 'error'
          present :message, 'You need to be logged in to log out!'
        end
      end

    end
  end
end
