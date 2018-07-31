class User < ApplicationRecord
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable and :omniauthable
  has_many :clouds
  has_many :tokens
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable,
         :omniauthable, omniauth_providers: [:facebook, :google_oauth2,:twitter, :linkedin]



  def self.from_omniauth_facebook(auth)
    where(provider: auth.provider, uid: auth.uid).first_or_create do |user|
      user.email = auth.info.email
      user.provider = data.to_json
      user.password = Devise.friendly_token[0,20]
    end
  end

  def self.from_omniauth_google(access_token)
      data = access_token.info
      where(email: data['email']).first_or_create do |user|
        user.email = data['email']
        user.provider = data.to_json
        user.password = Devise.friendly_token[0,20]
      end
  end
end
